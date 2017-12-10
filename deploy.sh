#!/bin/bash
export ANDROID_HOME="$HOME/Android/Sdk"
echo "Using SDK from $ANDROID_HOME"
cd PhotoOrganizer
chmod +x ./gradlew
echo "Running gradlew installDebug..."
./gradlew installDebug

echo "Deploying backend..."
cd ..
cd OrganizerBackend
chmod +x ./deploy.sh
./deploy.sh
