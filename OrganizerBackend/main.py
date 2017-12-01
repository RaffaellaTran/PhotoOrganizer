from flask import Flask, request, jsonify, Response
#from flask.ext.api import status
import google.auth.transport.requests
import google.oauth2.id_token
from firebase import firebase
from google.cloud import datastore, storage, vision
from datetime import datetime
from settings import *
import firebase_admin
from firebase_admin import auth, credentials
import uuid

app = Flask(__name__)

cred = credentials.Certificate(FIREBASE_ADMIN_JSON)
firebase_admin.initialize_app(cred)


@app.route('/create_group', methods=['POST'])
def create_group():

    data = request.values

    try:
        id_token = data['token']
        decoded_token = auth.verify_id_token(id_token)
        uid = decoded_token['uid']
    except ValueError:
        return jsonify("Token has expired or was not included"), 401

    fb = firebase.FirebaseApplication(FIREBASE_PROJECT_URL, None)

    group_name = data['group_name']
    expiration_time = data['expiration_time']
    user = data['user']

    putdata = {'owner': uid, 'expiration_time': expiration_time, 'join_token': uuid.uuid4().hex, 'users': [] }
    response = fb.put('/groups', group_name, putdata)
    push_response = fb.put('/groups/' + group_name + '/users/', uid, user)

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


    fb = firebase.FirebaseApplication(FIREBASE_PROJECT_URL, None)

    join_token = data['join_token']
    group_name = data['group_name']
    usir = data['user']

    group = fb.get('/groups/' + group_name + '/join_token', None)


    if group == join_token:
        response = fb.put('/groups/' + group_name + '/users/', uid, user)
        set_new = fb.put('/groups/' + group_name, 'join_token', uuid.uuid4().hex)
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

    fb = firebase.FirebaseApplication(FIREBASE_PROJECT_URL, None)

    group_name = data['group_name']

    response = fb.delete('/groups/' + group_name + '/users', uid)
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


    fb = firebase.FirebaseApplication(FIREBASE_PROJECT_URL, None)

    try:
        #Get image from request
        if 'imagefile' not in request.files:
            return jsonify({
                'api_internal_error': False,
                'error': 'HTTP/400 Bad request'
            }), 400
        img = request.files['imagefile']
        image_name = data['identifier']

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


        #Init gcloud vision client
        vision_client = vision.ImageAnnotatorClient()

        #Get response and labels from vision API
        response = client.annotate_image({'image': {'source': {'image_uri': 'gs://' + FIREBASE_BUCKET_URL + '/' + image_name }},
                    'features': [{'type': vision.enums.Feature.Type.FACE_DETECTION}], })


        return jsonify(response.annotations)



    except Exception as err:
        return jsonify(err)

    return response
