#!/bin/bash

###############################################################################
# Docker & PostgreSQL Installation Script
# This script will:
# 1. Install Docker if not already installed
# 2. Set up PostgreSQL in Docker with PostGIS extension
# 3. Check existing configurations and skip if identical
###############################################################################

set -e  # Exit on any error

# Color codes for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

###############################################################################
# Configuration
###############################################################################

# Database Configuration (can be overridden with environment variables)
DB_IP=${DB_IP:-0.0.0.0}
DB_PORT=${DB_PORT:-5432}
DB_NAME="irs"
DB_USER=${DB_USER:-postgres}
DB_PASS=${DB_PASS:-"admin123/?"}
POSTGRES_VERSION="17"
CONTAINER_NAME="postgres-irs"

###############################################################################
# Helper Functions
###############################################################################

print_success() {
    echo -e "${GREEN}✓ $1${NC}"
}

print_error() {
    echo -e "${RED}✗ $1${NC}"
}

print_info() {
    echo -e "${YELLOW}ℹ $1${NC}"
}

print_config() {
    echo -e "${BLUE}▶ $1${NC}"
}

print_header() {
    echo ""
    echo "═══════════════════════════════════════════════════════════════"
    echo "  $1"
    echo "═══════════════════════════════════════════════════════════════"
    echo ""
}

###############################################################################
# Display Configuration
###############################################################################

show_configuration() {
    print_header "Configuration Settings"
    
    print_config "Database Configuration:"
    echo "  Host/IP:        $DB_IP"
    echo "  Port:           $DB_PORT"
    echo "  Database Name:  $DB_NAME"
    echo "  Username:       $DB_USER"
    echo "  Password:       $DB_PASS"
    echo "  Container Name: $CONTAINER_NAME"
    echo "  PostgreSQL:     $POSTGRES_VERSION with PostGIS 3.4"
    echo ""
    
    read -p "Continue with this configuration? (Y/n): " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Nn]$ ]]; then
        print_info "Setup cancelled by user"
        exit 0
    fi
}

###############################################################################
# Java 17 Installation
###############################################################################

install_java() {
    print_header "Java 17 Installation"
    
    if command -v java &> /dev/null; then
        JAVA_VERSION=$(java -version 2>&1 | head -n 1)
        print_info "Java is already installed: $JAVA_VERSION"
        
        # Check if it's Java 17
        if java -version 2>&1 | grep -q "17\."; then
            print_success "Java 17 is already installed"
            return 0
        else
            print_info "Different Java version detected, installing Java 17..."
        fi
    else
        print_info "Java not found, installing Java 17..."
    fi
    
    # Detect OS
    if [ -f /etc/os-release ]; then
        . /etc/os-release
        OS=$ID
    else
        print_error "Cannot detect OS"
        exit 1
    fi
    
    case $OS in
        ubuntu|debian)
            sudo apt-get update
            sudo apt-get install -y openjdk-17-jdk openjdk-17-jre
            ;;
            
        centos|rhel|fedora)
            sudo yum install -y java-17-openjdk java-17-openjdk-devel
            ;;
            
        *)
            print_error "Unsupported OS: $OS"
            exit 1
            ;;
    esac
    
    # Verify installation
    if command -v java &> /dev/null; then
        JAVA_VERSION=$(java -version 2>&1 | head -n 1)
        print_success "Java installed successfully: $JAVA_VERSION"
        
        # Set JAVA_HOME
        export JAVA_HOME=$(dirname $(dirname $(readlink -f $(which java))))
        echo "export JAVA_HOME=$JAVA_HOME" >> ~/.bashrc
        print_info "JAVA_HOME set to: $JAVA_HOME"
    else
        print_error "Java installation failed"
        exit 1
    fi
}

###############################################################################
# Maven Installation
###############################################################################

install_maven() {
    print_header "Maven Installation"
    
    if command -v mvn &> /dev/null; then
        MVN_VERSION=$(mvn -version | head -n 1)
        print_info "Maven is already installed: $MVN_VERSION"
        print_success "Maven is ready"
        return 0
    fi
    
    print_info "Maven not found, installing..."
    
    # Detect OS
    if [ -f /etc/os-release ]; then
        . /etc/os-release
        OS=$ID
    else
        print_error "Cannot detect OS"
        exit 1
    fi
    
    case $OS in
        ubuntu|debian)
            sudo apt-get update
            sudo apt-get install -y maven
            ;;
            
        centos|rhel|fedora)
            sudo yum install -y maven
            ;;
            
        *)
            print_error "Unsupported OS: $OS"
            exit 1
            ;;
    esac
    
    # Verify installation
    if command -v mvn &> /dev/null; then
        MVN_VERSION=$(mvn -version | head -n 1)
        print_success "Maven installed successfully: $MVN_VERSION"
    else
        print_error "Maven installation failed"
        exit 1
    fi
}

###############################################################################
# Docker Installation
###############################################################################

install_docker() {
    print_header "Docker Installation"
    
    if command -v docker &> /dev/null; then
        DOCKER_VERSION=$(docker --version)
        print_info "Docker is already installed: $DOCKER_VERSION"
        
        # Check if Docker daemon is running
        if ! docker info &> /dev/null; then
            print_info "Starting Docker daemon..."
            sudo systemctl start docker
            sudo systemctl enable docker
            print_success "Docker daemon started"
        else
            print_success "Docker daemon is running"
        fi
        return 0
    fi
    
    print_info "Installing Docker..."
    
    # Detect OS
    if [ -f /etc/os-release ]; then
        . /etc/os-release
        OS=$ID
    else
        print_error "Cannot detect OS"
        exit 1
    fi
    
    case $OS in
        ubuntu|debian)
            # Update package index
            sudo apt-get update
            
            # Install prerequisites
            sudo apt-get install -y \
                ca-certificates \
                curl \
                gnupg \
                lsb-release
            
            # Add Docker's official GPG key
            sudo mkdir -p /etc/apt/keyrings
            curl -fsSL https://download.docker.com/linux/$OS/gpg | sudo gpg --dearmor -o /etc/apt/keyrings/docker.gpg
            
            # Set up repository
            echo \
              "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/$OS \
              $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
            
            # Install Docker
            sudo apt-get update
            sudo apt-get install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin
            ;;
            
        centos|rhel|fedora)
            # Install required packages
            sudo yum install -y yum-utils
            
            # Add Docker repository
            sudo yum-config-manager --add-repo https://download.docker.com/linux/centos/docker-ce.repo
            
            # Install Docker
            sudo yum install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin
            ;;
            
        *)
            print_error "Unsupported OS: $OS"
            exit 1
            ;;
    esac
    
    # Start and enable Docker
    sudo systemctl start docker
    sudo systemctl enable docker
    
    # Add current user to docker group
    sudo usermod -aG docker $USER
    
    print_success "Docker installed successfully"
    print_info "You may need to log out and back in for group changes to take effect"
}

###############################################################################
# PostgreSQL Setup
###############################################################################

check_postgres_config() {
    print_header "Checking PostgreSQL Configuration"
    
    # Check if container exists
    if ! docker ps -a --format '{{.Names}}' | grep -q "^${CONTAINER_NAME}$"; then
        print_info "PostgreSQL container does not exist"
        return 1
    fi
    
    # Check if container is running
    if ! docker ps --format '{{.Names}}' | grep -q "^${CONTAINER_NAME}$"; then
        print_info "PostgreSQL container exists but is not running"
        print_info "Starting existing container..."
        docker start $CONTAINER_NAME
        sleep 5
    fi
    
    # Check configuration
    EXISTING_USER=$(docker exec $CONTAINER_NAME psql -U postgres -tAc "SELECT current_user;")
    EXISTING_PORT=$(docker inspect $CONTAINER_NAME | grep -oP '"HostPort": "\K[0-9]+' | head -1)
    
    if [ "$EXISTING_PORT" == "$DB_PORT" ]; then
        print_info "Existing PostgreSQL configuration matches requirements"
        print_info "Port: $EXISTING_PORT"
        print_info "User: $EXISTING_USER"
        
        # Check if database exists
        DB_EXISTS=$(docker exec $CONTAINER_NAME psql -U postgres -tAc "SELECT 1 FROM pg_database WHERE datname='$DB_NAME';")
        if [ "$DB_EXISTS" == "1" ]; then
            print_success "Database '$DB_NAME' already exists"
            return 0
        fi
    else
        print_info "Configuration mismatch detected"
        print_info "Expected port: $DB_PORT, Found: $EXISTING_PORT"
        read -p "Do you want to remove and recreate the container? (y/N): " -n 1 -r
        echo
        if [[ $REPLY =~ ^[Yy]$ ]]; then
            docker stop $CONTAINER_NAME
            docker rm $CONTAINER_NAME
            return 1
        else
            print_info "Keeping existing configuration"
            return 0
        fi
    fi
    
    return 1
}

setup_postgres() {
    print_header "PostgreSQL Setup"
    
    if check_postgres_config; then
        print_success "PostgreSQL is already configured correctly"
        return 0
    fi
    
    print_info "Setting up PostgreSQL with PostGIS..."
    
    # Pull PostgreSQL image with PostGIS
    print_info "Pulling PostgreSQL $POSTGRES_VERSION with PostGIS image..."
    docker pull postgis/postgis:${POSTGRES_VERSION}-3.4
    
    # Run PostgreSQL container
    print_info "Starting PostgreSQL container..."
    docker run -d \
        --name $CONTAINER_NAME \
        -e POSTGRES_PASSWORD=$DB_PASS \
        -e POSTGRES_USER=$DB_USER \
        -e POSTGRES_DB=$DB_NAME \
        -p ${DB_PORT}:5432 \
        -v postgres-data:/var/lib/postgresql/data \
        --restart unless-stopped \
        postgis/postgis:${POSTGRES_VERSION}-3.4
    
    # Wait for PostgreSQL to be ready
    print_info "Waiting for PostgreSQL to be ready..."
    sleep 10
    
    for i in {1..30}; do
        if docker exec $CONTAINER_NAME pg_isready -U $DB_USER &> /dev/null; then
            print_success "PostgreSQL is ready"
            break
        fi
        echo -n "."
        sleep 2
    done
    echo ""
    
    # Install PostGIS extension
    print_info "Installing PostGIS extension..."
    docker exec $CONTAINER_NAME psql -U $DB_USER -d $DB_NAME -c "CREATE EXTENSION IF NOT EXISTS postgis;"
    
    # Verify PostGIS installation
    POSTGIS_VERSION=$(docker exec $CONTAINER_NAME psql -U $DB_USER -d $DB_NAME -tAc "SELECT postgis_version();")
    print_success "PostGIS installed: $POSTGIS_VERSION"
    
    print_success "PostgreSQL container created and started"
}

###############################################################################
# Verification
###############################################################################

verify_setup() {
    print_header "Verifying Setup"
    
    # Check Docker
    if docker ps | grep -q $CONTAINER_NAME; then
        print_success "PostgreSQL container is running"
    else
        print_error "PostgreSQL container is not running"
        return 1
    fi
    
    # Check database connectivity
    if docker exec $CONTAINER_NAME psql -U $DB_USER -d $DB_NAME -c "SELECT 1;" &> /dev/null; then
        print_success "Database connection successful"
    else
        print_error "Cannot connect to database"
        return 1
    fi
    
    # Check PostGIS
    if docker exec $CONTAINER_NAME psql -U $DB_USER -d $DB_NAME -tAc "SELECT postgis_version();" &> /dev/null; then
        print_success "PostGIS extension is installed"
    else
        print_error "PostGIS extension is not installed"
        return 1
    fi
    
    # Show connection details
    echo ""
    print_info "Database Connection Details:"
    echo "  Host: localhost (or $DB_IP for remote access)"
    echo "  Port: $DB_PORT"
    echo "  Database: $DB_NAME"
    echo "  Username: $DB_USER"
    echo "  Password: $DB_PASS"
    echo ""
    print_info "JDBC URL for Quarkus:"
    echo "  jdbc:postgresql://localhost:${DB_PORT}/${DB_NAME}"
    echo ""
}

###############################################################################
# Main Execution
###############################################################################

main() {
    print_header "IRS Server Setup - Complete Installation"
    
    print_info "This script will install:"
    print_info "  - Java 17 (OpenJDK)"
    print_info "  - Maven"
    print_info "  - Docker"
    print_info "  - PostgreSQL with PostGIS"
    echo ""
    
    # Show configuration and ask for confirmation
    show_configuration
    
    # Install Java 17
    install_java
    
    # Install Maven
    install_maven
    
    # Install Docker
    install_docker
    
    # Setup PostgreSQL
    setup_postgres
    
    # Verify setup
    verify_setup
    
    print_header "Installation Complete!"
    print_success "All components installed successfully:"
    echo ""
    
    # Show installed versions
    if command -v java &> /dev/null; then
        JAVA_VER=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2)
        echo "  ✓ Java:       $JAVA_VER"
    fi
    
    if command -v mvn &> /dev/null; then
        MVN_VER=$(mvn -version | head -n 1 | awk '{print $3}')
        echo "  ✓ Maven:      $MVN_VER"
    fi
    
    if command -v docker &> /dev/null; then
        DOCKER_VER=$(docker --version | awk '{print $3}' | tr -d ',')
        echo "  ✓ Docker:     $DOCKER_VER"
    fi
    
    echo "  ✓ PostgreSQL: 17 with PostGIS 3.4"
    echo ""
    
    print_info "Next step: Run './setup-database_universal.sh' to create schema and import data"
    echo ""
}

# Run main function
main
