from flask import Flask, request, jsonify, Response
#from flask.ext.api import status
import google.auth.transport.requests
import google.oauth2.id_token

import pyrebase

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

app = Flask(__name__)

cred = credentials.Certificate(FIREBASE_ADMIN_JSON)
firebase_admin.initialize_app(cred)
firebase = pyrebase.initialize_app(PYREBASE_CONFIG)

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

    users = {uid: user}
    putdata = {'owner': uid, 'expiration_time': expiration_time, 'join_token': group_name + ':' + uuid.uuid4().hex, 'users': users }

    response = fb.put('/groups', group_name, putdata)

    fb.put('/users/', uid, {'group': group_name})

    db = firebase.database()

    putdata = {group_name: {'owner': uid, 'expiration_time': expiration_time, 'join_token': group_name + ':' + uuid.uuid4().hex, 'users': [{uid:user}] }}
    response = db.child('groups').set(putdata)
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

    group = db.child('groups').child(group_name).child('/join_token').get().val()

    if group == join_token:
        response = fb.put('/groups/' + group_name + '/users/', uid, user)
        set_new = fb.put('/groups/' + group_name, 'join_token', uuid.uuid4().hex)
        fb.put('/users/' + uid, 'group', group_name)
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
        response = db.child('groups').child(group_name).remove()
    else:
        response = db.child('groups').child(group_name).child('users').child(uid).remove()
    return jsonify(response)



@app.route('/label', methods=['POST'])
def label():

    data = request.values

    #try:
    #    id_token = data['token']
    #    decoded_token = auth.verify_id_token(id_token)
    #    uid = decoded_token['uid']
    #except ValueError:
    #    return jsonify("Token has expired or was not included"), 401


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
                small = resizeimage.resize_contain(img, IMAGE_SIZE_SMALL)
                small = small.convert("RGB")
                small.save(small_path, img.format)
                large = resizeimage.resize_contain(img, IMAGE_SIZE_LARGE)
                large = large.convert("RGB")
                large.save(large_path, img.format)

        #Get storage bucket
        bucket = storage_client.get_bucket(FIREBASE_BUCKET_URL)

        picture_blob = bucket.blob(image_name)

        #Upload picture from file to cloud storage
        picture_blob.upload_from_filename(path)

        small_bucket = storage_client.get_bucket(FIREBASE_BUCKET_SMALL)
        large_bucket = storage_client.get_bucket(FIREBASE_BUCKET_LARGE)
        blob_small = small_bucket.blob(image_name)
        blob_large = large_bucket.blob(image_name)

        blob_small.upload_from_filename(small_path)
        blob_large.upload_from_filename(large_path)


        os.remove(path)
        os.remove(small_path)
        os.remove(large_path)



        #Init gcloud vision client
        vision_client = vision.ImageAnnotatorClient()
        image = types.Image()
        image.source.image_uri = "gs://" + FIREBASE_BUCKET_URL + '/' + image_name

        #Get response and labels from vision API
        response = vision_client.face_detection(image = image)

        faces = False
        if len(response.face_annotations) != 0:
            faces = True

        picture_json = {"owner" : uid, "bucket_identifier" : image_name, "faces" : faces  }
        res = db.child('pictures').child(group).push(picture_json)

        return jsonify(res)

    except Exception as err:
        return jsonify(str(err)), 400
