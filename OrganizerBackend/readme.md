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
