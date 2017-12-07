FROM gcr.io/google-appengine/python
MAINTAINER Sami Lappalainen "sami.lappalainen@aalto.fi"
RUN apt-get update -y
RUN apt-get install -y python-pip python-dev build-essential
COPY . /app
WORKDIR /app
RUN pip install -r requirements.txt
ENV FLASK_APP=main.py

ENTRYPOINT ["gunicorn"]
CMD ["-b", ":$PORT", "main:app"]
