#!/bin/bash

# Exit script on any error
set -e

# Set the default repository name
DEFAULT_REPO_NAME="data-access"

# Set the default repository name
DEFAULT_DB_IP="0.0.0.0"


# Use the provided repository name or fall back to the default
REPO_NAME=${1:-$DEFAULT_REPO_NAME}

# Construct the repository URL dynamically
REPO_URL="https://github.com/usamazahid/${REPO_NAME}"

# Define the Docker image name
IMAGE_NAME="quarkus/${REPO_NAME}-jvm"

# Define the environment variable
DB_IP=${2:-$DEFAULT_DB_IP}  # Replace with the actual DB IP

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

# Export the environment variable for DB_IP
export DB_IP=$DB_IP

# Build the project
echo "Building the project..."
./mvnw package

# Build the Docker image
echo "Building the Docker image..."
docker build -f src/main/docker/Dockerfile.jvm -t $IMAGE_NAME .

# Run the Docker container in detached mode
echo "Running the Docker container in the background..."
docker run -d --rm -p 8080:8080 -e JAVA_DEBUG=true -e JAVA_DEBUG_PORT=*:5005 $IMAGE_NAME

echo "Container for '$IMAGE_NAME' is running in the background on port 8080."
