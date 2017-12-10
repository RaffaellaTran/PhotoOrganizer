# PhotoOrganizer
Group 8

## Implemented features

 - All required frontend and backend requirements are completed.
 - As extra work, we created a web application.

## Project structure

 - Makefile: Used for deploying this application
 - OrganizerBackend/
    - Contains the backend made with Flask and served with Gunicorn
    - Main.py
    - deploy.sh: Used for deploying to the Google Cloud
    - TODO: Explain the most important files here...?
 - PhotoOrganizer/
    - /install.sh: Builds and installs this Application to a connected Android device
    - /java/
        - AddMemberActivity: display a QR code for new members to join
        - ApiHttp: a simple OkHttp wrapper to handle communication to backend
        - CameraActivity: take a photo. Uses local resources to recognize any bar codes,
            and uploads images to the group only if no bar codes are found. Public images
            are uploaded only if user is in a group - otherwise this step is cancelled.
            The upload size is fetched from user settings.
        - CreateGroupActivity: allows user to create a new group if they are not a member
            of any group. User needs to enter group name and time to keep group valid.
            In group name, we recommend using only latin characters and numbers.
        - CustomGridAdapter: adapter for the main menu list. Sets menu items and their
            actions.
        - GalleryAcitivity: Displays all albums
        - GalleryAlbumAcitivity: Displays all pictures in album
        - GalleryFullscreenActivity: Displays one image in fullscreen
        - Group: group representation. Method getMyGroup fetches user's current group
            from Firebase.
        - GroupManagementActivity: If user is in a group, this activity displays group
            name, expiration time, and group members, with options to leave the group
            or display a QR code (see AddMemberActivity) for a new member to join the
            group. Else, user has the options to create a new group or join an existing
            one by scanning a QR code.
        - JoinActivity: launch camera and look for QR codes. The user sees the camera
            preview in screen, and once a valid QR code is detected, the app starts
            group joining process in the background automatically. See also
            QRCameraPreview.
        - MainActivity: the starting point of the app. See also CustomGridAdapter.
        - QRCameraPreview: a view to display camera preview when user is about to join
            a group. See also JoinActivity.
        - User: representation of a Firebase/App user.
        TODO: Explain the most important files here...?
 - Web app/
    - TODO:
        
## Usage instructions

This project includes a Makefile for easy deployment. 

Run ``make help`` to see all commands.

#### Prerequisities

- You need to connect your Android device
- You may need to edit the Android SDK path in ``/PhotoOrganizer/install.sh``
    - The default path is "$HOME/Android/Sdk"

#### Deploying Backend and Frontend
By running the following command, Backend will be deployed to the Google Cloud and
the Android application will be installed to a connected Android device:

    make deploy

#### Frontend only
The Application can be built and installed as follows:

    make android

#### Backend only
The Backend can be deployed to the Google Cloud as follows:

    make backend
    
#### Backend only (docker)
There is also a possibility to deploy the backend as a Docker image to the Google Cloud. This can be done as follows:

    make backend-docker

