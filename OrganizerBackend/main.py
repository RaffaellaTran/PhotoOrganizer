from flask import Flask, request, jsonify, Response
#from flask.ext.api import status
import google.auth.transport.requests
import google.oauth2.id_token
from firebase import firebase
from google.cloud import datastore, storage, vision, types
from datetime import datetime
from settings import *
from firebase_admin import auth
import io
import os



app = Flask(__name__)


@app.route('/create_group', methods=['POST'])
def create_group():

    data = request.values

    id_token = data['token']
    decoded_token = auth.verify_token(id_token)
    uid = decoded_token['uid']

    fb = firebase.FirebaseApplication(FIREBASE_PROJECT_URL, None)

    group_name = data['group_name']
    expiration_time = data['expiration_time']
    owner = data['owner']

    putdata = {'owner': owner, 'expiration_time': expiration_time }
    response = fb.put('/groups',group_name, putdata)
    return jsonify(response)




#Join
@app.route('/join_group', methods=['POST'])
def join_group():
    data = request.values

    #Firebase auth
    id_token = data['token']
    decoded_token = auth.verify_token(id_token)
    uid = decoded_token['uid']

    fb = firebase.FirebaseApplication(FIREBASE_PROJECT_URL, None)

    join_token = data['join_token']
    group_name = data['group_name']
    user = data['user']

    putdata = {user : 'true'}
    response = fb.put('/groups/' + group_name, user, True)
    return jsonify(response)


@app.route('/leave_group', methods=['DELETE'])
def leave_group():


    data = request.values

    #Firebase auth
    id_token = data['token']
    decoded_token = auth.verify_token(id_token)
    uid = decoded_token['uid']

    fb = firebase.FirebaseApplication(FIREBASE_PROJECT_URL, None)

    user = data['user']
    group_name = data['group_name']

    response = fb.delete('/groups/' + group_name, user)
    return jsonify(response)




@app.route('/label', methods=['POST'])
def label():

    data = request.values

    #Firebase auth
    id_token = data['token']
    decoded_token = auth.verify_token(id_token)
    uid = decoded_token['uid']


    fb = firebase.FirebaseApplication(FIREBASE_PROJECT_URL, None)

    try:
        #Get image from request
        if('imagefile' not in request.files)
            return jsonify({
                'api_internal_error': False,
                'error': 'HTTP/400 Bad request'
            }), 400
        img = request.files['imagefile']
        image_name = data['identifier']
        #Init gcloud vision client
        vision_client = vision.ImageAnnotatorClient()
        image = types.Image(content = img)

        #Get response and labels from vision API
        response = client.label_detection(image = image)
        labels = client.label_annotations

        #Init storage client for labeled pictures
        storage_client = storage.Client()

        #Get storage bucket
        bucket = storage_client.get_bucket(FIREBASE_BUCKET_URL)
        picture_blob = bucket.get_blob(image_name)

        #Save file temporarily
        path = os.path.join(FILE_TEMP_DIR, image_name)
        img.save(path)

        #Upload picture from file to cloud storage
        picture_blob.upload_from_file(path)

        for label in lables:
            print(label)




    except Exception as err:
        return jsonify(err)

    return response
