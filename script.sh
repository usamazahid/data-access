#!/bin/bash

# Exit script on any error
set -e

DEFAULT_DIRECTORY_FILES="/external-storage/accident-reports"
# Set the default repository name
DEFAULT_REPO_NAME="data-access"

# Set the default DB IP
DEFAULT_DB_IP="0.0.0.0"

# Use the provided repository name or fall back to the default
REPO_NAME=${1:-$DEFAULT_REPO_NAME}

# Construct the repository URL dynamically
REPO_URL="https://github.com/usamazahid/${REPO_NAME}"

# Define the Docker image name
IMAGE_NAME="quarkus/${REPO_NAME}-jvm"

# Use the provided DB IP or fall back to the default
DB_IP=${2:-$DEFAULT_DB_IP}

# Check if we're in the repository directory
if [ "$(basename $PWD)" == "$REPO_NAME" ]; then
  echo "Already in the '$REPO_NAME' directory. Pulling latest changes..."
  git pull origin main
else
  # Check if the directory exists
  if [ -d "$REPO_NAME" ]; then
    echo "'$REPO_NAME' directory exists. Entering the directory and pulling latest changes..."
    cd $REPO_NAME
    git pull origin main
  else
    # Clone the repository if the directory does not exist
    echo "'$REPO_NAME' directory does not exist. Cloning the repository..."
    git clone $REPO_URL
    cd $REPO_NAME
  fi
fi

# Build the project
echo "Building the project..."
mvn package

# Build the Docker image
echo "Building the Docker image..."
docker build -f src/main/docker/Dockerfile.jvm -t $IMAGE_NAME .

# Stop and remove any running container using the same image
EXISTING_CONTAINER=$(docker ps -q --filter ancestor=$IMAGE_NAME)

if [ ! -z "$EXISTING_CONTAINER" ]; then
  echo "Stopping and removing the existing container for image '$IMAGE_NAME'..."
  docker stop $EXISTING_CONTAINER
fi

# Check if the directory exists
if [ ! -d "$DEFAULT_DIRECTORY_FILES" ]; then
  echo "Directory '$DEFAULT_DIRECTORY_FILES' does not exist. Creating it..."
  mkdir -p "$DEFAULT_DIRECTORY_FILES"
  # Set permissions for the directory
  echo "Setting permissions for '$DEFAULT_DIRECTORY_FILES'..."
  chmod 777 "$DEFAULT_DIRECTORY_FILES"
else
  echo "Directory '$DEFAULT_DIRECTORY_FILES' already exists."
fi

# Stop and remove any container using port 8080
EXISTING_PORT_CONTAINER=$(docker ps -q --filter "publish=8080")

if [ ! -z "$EXISTING_PORT_CONTAINER" ]; then
  echo "Stopping and removing the existing container using port 8080..."
  docker stop $EXISTING_PORT_CONTAINER
fi

# Run the Docker container in detached mode, passing DB_IP as an environment variable
echo "Running the Docker container in the background, exposing DB_IP..."
docker run -d --rm -p 8080:8080 \
  -e JAVA_DEBUG=true \
  -e JAVA_DEBUG_PORT=*:5005 \
  -e DB_IP=${DB_IP} \
  -v ${DEFAULT_DIRECTORY_FILES}:${DEFAULT_DIRECTORY_FILES} \
  $IMAGE_NAME

echo "Container for '$IMAGE_NAME' is running in the background on port 8080 with DB_IP='$DB_IP'."