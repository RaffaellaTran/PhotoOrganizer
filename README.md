# PhotoOrganizer
Group 8


## Project structure

 - OrganizerBackend/
    Contains the backend made with Flask and served with Gunicorn
    TODO: Explain the most important files here...?
 - PhotoOrganizer/
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

TODO
```
git clone
cd mcc-2017-g08
make deploy-backend
make deploy-frontend
```
