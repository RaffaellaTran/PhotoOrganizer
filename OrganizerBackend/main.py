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

app = Flask(__name__)

cred = credentials.Certificate(FIREBASE_ADMIN_JSON)
firebase_admin.initialize_app(cred)


@app.route('/create_group', methods=['POST'])
def create_group():

    data = request.values

    id_token = data['token']
    decoded_token = auth.verify_id_token(id_token)
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

    id_token = data['token']
    decoded_token = auth.verify_id_token(id_token)
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

    id_token = data['token']
    decoded_token = auth.verify_id_token(id_token)
    uid = decoded_token['uid']

    fb = firebase.FirebaseApplication(FIREBASE_PROJECT_URL, None)


    user = data['user']
    group_name = data['group_name']

    response = fb.delete('/groups/' + group_name, user)
    return jsonify(response)


@app.route('/label', methods=['POST'])
def label():

    data = request.values
    files = request.files

    id_token = data['token']
    decoded_token = auth.verify_id_token(id_token)
    uid = decoded_token['uid']


    fb = firebase.FirebaseApplication(FIREBASE_PROJECT_URL, None)

    if not 'picture' in files:
        return jsonify(""), 400
