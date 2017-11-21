from flask import Flask, request, jsonify
from flask.ext.api import status
import google.auth.transport.requests
import google.oauth2.id_token
from firebase import firebase
from google.cloud import datastore, storage, vision
from datetime import datetime
from settings import *
from firebase_admin import auth

app = Flask(__name__)
global count = 1


#
@app.route('/create_group', methods=['POST'])
def create_group():

    fb = firebase.FirebaseApplication(FIREBASE_PROJECT_URL, None)
    group_name = request.group_name
    expiration_time = request.expiration_time
    owner = request.owner
    putdata = {'group_name' : group_name, 'owner': owner, 'expiration_time': expiration_time }
    fb.put('/groups', group_name, putdata)
    return status.HTTP_200_OK




#Join
@app.route('/join_group', methods=['GET', 'POST'])
def join_group():
    return status.HTTP_204_NO_CONTENT



@app.route('/label', methods=['GET','POST'])
def label():
    #photo = request.files['file']



    fb = firebase.FirebaseApplication(FIREBASE_PROJECT_URL, None)
    test = fb.get('/test', None)

    current_datetime = datetime.now()

    label = "Faces"
    vision_client = vision.Client()

    return 'Test'
