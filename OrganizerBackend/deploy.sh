#!/bin/bash
set -x
if [ $1 == 'docker' ]; then
    mv Dockerfile_temp Dockerfile
    docker build -t test:latest .
    docker tag test us.gcr.io/mcc-fall-2017-g08/test
    gcloud docker -- push us.gcr.io/mcc-fall-2017-g08/test
    gcloud app deploy --image-url us.gcr.io/mcc-fall-2017-g08/test
    gcloud app deploy cron.yaml
    gcloud app logs tail -s default
else
    mv Dockerfile Dockerfile_temp
    gcloud app deploy
    gcloud app deploy cron.yaml
    gcloud app logs tail -s default
fi
