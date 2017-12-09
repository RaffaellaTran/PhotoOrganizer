#Run the following commands to run the initial Flask project on a local system

#Create a virtual environment using virtualenv
$ virtualenv venv
$ source venv/bin/activate


#Navigate to OrganizerBackend and resolve dependencies
$ pip install -r requirements.txt

#After everything has been installed successfully run the following command
#to add the flask project main.py to path
$ export FLASK_APP=main.py

#Run the server
$ flask run

#If successful you should see the following message
$ flask run
    * Running on http://127.0.0.1:5000/


#Should probably work...



#Working API routes:

The API accepts HTTP [POST, GET, DELETE] requests at the moment

[POST] localhost:5000/create_group
required fields:
token : {firebase auth token}
group_name : {name of the group to be created}
expiration_time : {YYYY-MM-DD HH:SS format}
user: {name of the user to be displayed to other users}

[POST] localhost:5000/join_group
required fields:
token : {firebase auth token}
join_token : {single use token from group info}
user: {name of the user to be displayed to other users}
group_name : {name of the group to be joined}

[DELETE] localhost:5000/leave_group
token : {firebase auth token}
group_name : {name of the group to be left from}


[POST] localhost:5000/label
token : {firebase auth token}
group_name : {name of the group to be left from}
imagefile : {file that is to be uploaded}


#STEPS TO DEPLOY TO gcloud
0. Install gcloud SDK and authenticate
1. Set permissions on deploy.sh script
2. Run $ ./deploy.sh
3. ???
4. profit

## Windows (by Aleksi)

```
' Install Python and set PATH
set PATH=%PATH%;C:\Python27
set PATH=%PATH%;C:\Python27\Scripts
' Activate virtualenv
cd %HOMEPATH%
pip install virtualenv
virtualenv venv
venv\Scripts\activate
' Setup backend
cd ProjectBackend
pip install -r requirements.txt
set FLASK_APP=main.py
flask run
```
