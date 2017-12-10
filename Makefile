help:
	@echo "Please use \`make <target>' where <target> is one of"
	@echo "  deploy  		to deploy backend to Google Cloud and install application to Android device"
	@echo "  android  		to install application to Android device"
	@echo "  backend 		to deploy backend to Google Cloud"
	@echo "  backend-docker 	to deploy backend with a custom Dockerfile to Google Cloud"


deploy: android backend

android:
	@echo ""
	@echo ""
	@echo "Installing PhotoOrganizer"
	@echo ""
	cd PhotoOrganizer && \
	chmod +x install.sh && \
	./install.sh

backend:
	@echo ""
	@echo ""
	@echo "Deploying Backend to Google cloud"
	@echo ""
	cd OrganizerBackend && \
	chmod +x deploy.sh && \
	./deploy.sh normal

backend-docker:
	@echo ""
	@echo ""
	@echo "Deploying docker version of Backend to Google cloud"
	@echo ""
	cd OrganizerBackend && \
	chmod +x deploy.sh && \
	./deploy.sh docker
