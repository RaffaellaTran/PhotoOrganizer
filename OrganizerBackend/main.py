from flask import Flask, request, jsonify, Response
#from flask.ext.api import status
import google.auth.transport.requests
import google.oauth2.id_token

import pyrebase
import json

from google.cloud import datastore, storage, vision
from google.cloud.vision import types
from datetime import datetime
from settings import *

import firebase_admin
from firebase_admin import auth, credentials
import uuid
import os
from werkzeug.utils import secure_filename

from PIL import Image
from resizeimage import resizeimage

from pyfcm import FCMNotification

app = Flask(__name__)

cred = credentials.Certificate(FIREBASE_ADMIN_JSON)
firebase_admin.initialize_app(cred)
firebase = pyrebase.initialize_app(PYREBASE_CONFIG)

push_service = FCMNotification(api_key=MESSAGING_API_KEY)

#print firebase.auth().sign_in_with_email_and_password('kahvipuu@gmail.com', 'liDeech9ev')

@app.route('/create_group', methods=['POST'])
def create_group():

    data = request.values

    try:
        id_token = data['token']
        decoded_token = auth.verify_id_token(id_token)
        uid = decoded_token['uid']
    except ValueError:
        return jsonify("Token has expired or was not included"), 401


    group_name = data['group_name']
    expiration_time = data['expiration_time']
    user = data['user']
    firebase.auth()
    db = firebase.database()

    putdata = {group_name: {'owner': uid, 'expiration_time': expiration_time, 'join_token': group_name + ':' + uuid.uuid4().hex, 'users': {uid:user} }}
    response = db.child('groups').update(putdata)
    update_group = db.child('users').update({uid:{'group':group_name}})
    return jsonify(response)



#Join
@app.route('/join_group', methods=['POST'])
def join_group():
    data = request.values

    try:
        id_token = data['token']
        decoded_token = auth.verify_id_token(id_token)
        uid = decoded_token['uid']
    except ValueError:
        return jsonify("Authorization token has expired or was not included"), 401


    join_token = data['join_token']
    group_name = data['group_name']
    user = data['user']

    db = firebase.database()

    group = db.child('groups').child(group_name).child('join_token').get().val()

    if group == join_token:

        response = db.child('groups').child(group_name).child('users').update({uid:user})
        set_new = db.child('groups').child(group_name).child('join_token').set(group_name + ':' + uuid.uuid4().hex)
        update_group = db.child('users').update({uid:{'group':group_name}})

        # Send push notification
        users = db.child('groups').child(group_name).child('users').get().val()
        message = user + " has joined your group!"
        for member_uid in users.keys():
            if member_uid != uid:
                # Notify all members except the current user
                result = push_service.notify_topic_subscribers(topic_name=member_uid, message_body=message)
                print(result)

        return jsonify(response)

    else:
        return jsonify({'error': 'The join token has expired, or is not valid'}), 400



@app.route('/leave_group', methods=['DELETE'])
def leave_group():
    data = request.values

    try:
        id_token = data['token']
        decoded_token = auth.verify_id_token(id_token)
        uid = decoded_token['uid']
    except ValueError:
        return jsonify("Token has expired or was not included"), 401

    db = firebase.database()

    group_name = data['group_name']
    group_uri = '/groups/' + group_name

    get = db.child('groups').child(group_name).child('owner').get().val()

    if get == uid:
        response = clean_group_and_data(db, group_name)
        #response = db.child('groups').child(group_name).remove()
    else:
        response = db.child('groups').child(group_name).child('users').child(uid).remove()

    update_group = db.child('users').child(uid).remove()


    return jsonify(response)



@app.route('/label', methods=['POST'])
def label():

    data = request.values

    try:
        id_token = data['token']
        decoded_token = auth.verify_id_token(id_token)
        uid = decoded_token['uid']
    except ValueError:
        return jsonify("Token has expired or was not included"), 401


    db = firebase.database()

    try:
        #Get image from request
        if 'imagefile' not in request.files:
            return jsonify({
                'api_internal_error': False,
                'error': 'HTTP/400 Bad request'
            }), 400


        img = request.files['imagefile']
        group = data['group_name']
        image_name = secure_filename(img.filename)

        #Init storage client for labeled pictures
        storage_client = storage.Client()


        #Save file temporarily
        path = os.path.join(FILE_TEMP_DIR, image_name)
        small_path = os.path.join(FILE_TEMP_DIR, "small_" + image_name)
        large_path = os.path.join(FILE_TEMP_DIR, "large_" + image_name)

        img.save(path)

        #Save files temporarily
        with open(path, 'r+b') as img_file:
            with Image.open(img_file) as img:
                if(img.size[0] > IMAGE_SIZE_SMALL):
                    small = resizeimage.resize_contain(img, IMAGE_SIZE_SMALL)
                    small = small.convert("RGB")
                else:
                    small = img
                small.save(small_path, img.format)

                if(img.size[0] > IMAGE_SIZE_LARGE):
                    large = resizeimage.resize_contain(img, IMAGE_SIZE_LARGE)
                    large = large.convert("RGB")
                else:
                    large = img
                large.save(large_path, img.format)

        #Get storage bucket
        bucket = storage_client.get_bucket(FIREBASE_BUCKET_URL)
        storage_uid = uuid.uuid4().hex + '.' + img.format
        picture_blob = bucket.blob(storage_uid)
        picture_blob.content_type = 'image/' + img.format

        #Upload picture from file to cloud storage
        picture_blob.upload_from_filename(path)
        picture_blob.patch()

        small_bucket = storage_client.get_bucket(FIREBASE_BUCKET_SMALL)
        large_bucket = storage_client.get_bucket(FIREBASE_BUCKET_LARGE)
        blob_small = small_bucket.blob(storage_uid)
        blob_large = large_bucket.blob(storage_uid)

        blob_small.content_type = 'image/' + img.format
        blob_small.upload_from_filename(small_path)
        blob_small.patch()

        blob_large.upload_from_filename(large_path)
        blob_large.content_type = 'image/' + img.format
        blob_large.patch()

        os.remove(path)
        os.remove(small_path)
        os.remove(large_path)



        #Init gcloud vision client
        vision_client = vision.ImageAnnotatorClient()
        image = types.Image()
        image.source.image_uri = "gs://" + FIREBASE_BUCKET_URL + '/' + storage_uid

        #Get response and labels from vision API
        response = vision_client.face_detection(image = image)

        faces = False
        if len(response.face_annotations) != 0:
            faces = True

        picture_json = {"owner" : uid, "bucket_identifier" : storage_uid, "faces" : faces  }
        res = db.child('pictures').child(group).push(picture_json)

        # Send push notification to all members except the current user
        users = db.child('groups').child(group).child('users').get().val()
        message = "A new picture was posted to your group!"
        for member_uid in users.keys():
            if member_uid != uid:
                result = push_service.notify_topic_subscribers(topic_name=member_uid, message_body=message)
                print(result) # For debugging

        return jsonify(res)

    except Exception as err:
        return jsonify(str(err)), 400

def clean_group_and_data(db, grp_name):
    storage_client = storage.Client()
    bucket = storage_client.get_bucket(FIREBASE_BUCKET_URL)
    small_bucket = storage_client.get_bucket(FIREBASE_BUCKET_SMALL)
    large_bucket = storage_client.get_bucket(FIREBASE_BUCKET_LARGE)

    pics = db.child('pictures').child(grp_name).get()
    if pics.each() is not None:
        for pic in pics.each():
            id = pic.val()['bucket_identifier']
            bucket_remove_blobs(id, bucket, small_bucket, large_bucket)

    users = db.child('groups').child(grp_name).child('users').get()

    if users.each() is not None:
        for usr in users.each():
            db.child('users').child(usr.key()).remove()

    db.child('pictures').child(grp_name).remove()
    return db.child('groups').child(grp_name).remove()


def bucket_remove_blobs(id, bucket, small_bucket, large_bucket):
    try:
        blb = bucket.blob(id)
        if blb != None:
            blb.delete()

        blb = small_bucket.blob(id)
        if blb != None:
            blb.delete()

        blb = large_bucket.blob(id)
        if blb != None:
            blb.delete()
    except Exception:
        pass

@app.route('/clean', methods=['GET'])
def clean():
    #if request.headers['X-Appengine-Cron'] != 'true':
    #    return jsonify('Error'), 201
    firebase.auth()
    db = firebase.database()
    groups = db.child('groups').get()

    if groups.each() is not None:
        for group in groups.each():
            grp_name = group.key()
            grp = group.val()
            exp_time = datetime.strptime(grp['expiration_time'], '%Y-%m-%d %H:%M:%S')

            if(datetime.now() > exp_time):
                clean_group_and_data(db, grp_name)
        return jsonify('Cleanup done'), 200
    else:
        return jsonify('No groups'), 201
