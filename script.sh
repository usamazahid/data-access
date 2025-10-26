#!/bin/bash

# Exit script on any error
set -e

DEFAULT_DIRECTORY_FILES="/external-storage/accident-reports"
DEFAULT_REPO_NAME="data-access"
DEFAULT_DB_IP="host.docker.internal"
DEFAULT_FLASK_IP="host.docker.internal"
DEFAULT_FLASK_PORT="5000"

REPO_NAME=${1:-$DEFAULT_REPO_NAME}
DB_IP=${2:-$DEFAULT_DB_IP}
FLASK_APP_IP=${3:-$DEFAULT_FLASK_IP}
FLASK_APP_PORT=${4:-$DEFAULT_FLASK_PORT}

REPO_URL="https://github.com/usamazahid/${REPO_NAME}"
IMAGE_NAME="quarkus/${REPO_NAME}-jvm"

# Check if we're in the repository directory
if [ "$(basename $PWD)" == "$REPO_NAME" ]; then
  echo "Already in the '$REPO_NAME' directory. Pulling latest changes..."
  git pull origin main
else
  if [ -d "$REPO_NAME" ]; then
    echo "'$REPO_NAME' directory exists. Entering the directory and pulling latest changes..."
    cd $REPO_NAME
    git pull origin main
  else
    echo "'$REPO_NAME' directory does not exist. Cloning the repository..."
    git clone $REPO_URL
    cd $REPO_NAME
  fi
fi

echo "Building the project..."
mvn package

echo "Building the Docker image..."
docker build -f src/main/docker/Dockerfile.jvm -t $IMAGE_NAME .

EXISTING_CONTAINER=$(docker ps -q --filter ancestor=$IMAGE_NAME)
if [ ! -z "$EXISTING_CONTAINER" ]; then
  echo "Stopping existing container using image '$IMAGE_NAME'..."
  docker stop $EXISTING_CONTAINER
fi

if [ ! -d "$DEFAULT_DIRECTORY_FILES" ]; then
  echo "Creating directory '$DEFAULT_DIRECTORY_FILES'..."
  mkdir -p "$DEFAULT_DIRECTORY_FILES"
  chmod 777 "$DEFAULT_DIRECTORY_FILES"
else
  echo "Directory '$DEFAULT_DIRECTORY_FILES' already exists."
fi

EXISTING_PORT_CONTAINER=$(docker ps -q --filter "publish=8080")
if [ ! -z "$EXISTING_PORT_CONTAINER" ]; then
  echo "Stopping container using port 8080..."
  docker stop $EXISTING_PORT_CONTAINER
fi

echo "Running the Docker container with host.docker.internal support..."
docker run -d --rm -p 8080:8080 \
  -e JAVA_DEBUG=true \
  -e JAVA_DEBUG_PORT=*:5005 \
  -e DB_IP=${DB_IP} \
  -e FLASK_APP_IP=${FLASK_APP_IP} \
  -e FLASK_APP_PORT=${FLASK_APP_PORT} \
  --add-host=host.docker.internal:host-gateway \
  -v ${DEFAULT_DIRECTORY_FILES}:${DEFAULT_DIRECTORY_FILES} \
  $IMAGE_NAME

echo "✅ Container for '$IMAGE_NAME' is running on port 8080"
echo "🌐 DB_IP: $DB_IP"
echo "🔗 Flask API URL: http://${FLASK_APP_IP}:${FLASK_APP_PORT}"
