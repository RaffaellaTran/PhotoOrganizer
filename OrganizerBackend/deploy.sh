#!/bin/bash
docker build -t test:latest .
docker tag test us.gcr.io/mcc-fall-2017-g08/test
gcloud docker -- push us.gcr.io/mcc-fall-2017-g08/test
gcloud app deploy --image-url us.gcr.io/mcc-fall-2017-g08/test cron.yaml
gcloud app logs tail -s default
