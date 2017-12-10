#!/bin/bash
export ANDROID_HOME="$HOME/Android/Sdk"
echo "Using SDK from $ANDROID_HOME"
chmod +x gradlew
echo "Running gradlew installDebug..."
./gradlew installDebug
