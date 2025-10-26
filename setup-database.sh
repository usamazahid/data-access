#!/bin/bash

###############################################################################
# Database Schema & Data Import Script
# This script will:
# 1. Create database schema from SQL files
# 2. Import CSV data (optional)
# 
# Prerequisites: Run setup-docker-postgres.sh first
###############################################################################

set -e  # Exit on any error

# Color codes for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

###############################################################################
# Configuration - MODIFY THESE PATHS AS NEEDED
###############################################################################

# Base directory (project root - automatically detected)
BASE_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

# Project relative paths
PROJECT_SQL_DIR="src/main/resources/script_queries"
PROJECT_CSV_DIR="src/main/resources/files"

# Full paths (BASE_DIR + project paths)
SQL_DIR="${BASE_DIR}/${PROJECT_SQL_DIR}"
CSV_DIR="${BASE_DIR}/${PROJECT_CSV_DIR}"

# Database Configuration (must match setup-docker-postgres.sh)
DB_IP=${DB_IP:-0.0.0.0}
DB_PORT=${DB_PORT:-5432}
DB_NAME="irs"
DB_USER=${DB_USER:-postgres}
DB_PASS=${DB_PASS:-"admin123/?"}
CONTAINER_NAME="postgres-irs"

# SQL files to execute (in order)
SQL_FILES=(
    "user.sql"
    "lov_schema.sql"
    "report.sql"
    "script.sql"
)

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
# Display Configuration & Verify Paths
###############################################################################

verify_paths() {
    print_header "Verifying Paths and Files"
    
    print_config "Base Directory (Project Root):"
    echo "  $BASE_DIR"
    echo ""
    
    print_config "SQL Scripts Directory:"
    echo "  Full Path:     $SQL_DIR"
    echo "  Project Path:  $PROJECT_SQL_DIR"
    if [ -d "$SQL_DIR" ]; then
        print_success "Directory exists"
    else
        print_error "Directory NOT found!"
        exit 1
    fi
    echo ""
    
    print_config "SQL Files to Execute:"
    local all_files_exist=true
    for sql_file in "${SQL_FILES[@]}"; do
        SQL_PATH="${SQL_DIR}/${sql_file}"
        if [ -f "$SQL_PATH" ]; then
            echo "  ✓ $sql_file"
        else
            echo "  ✗ $sql_file (NOT FOUND)"
            all_files_exist=false
        fi
    done
    
    if [ "$all_files_exist" = false ]; then
        print_error "Some SQL files are missing!"
        exit 1
    fi
    echo ""
    
    print_config "CSV Data Directory:"
    echo "  Full Path:     $CSV_DIR"
    echo "  Project Path:  $PROJECT_CSV_DIR"
    if [ -d "$CSV_DIR" ]; then
        print_success "Directory exists"
        
        # Count CSV files
        CSV_COUNT=$(find "$CSV_DIR" -maxdepth 1 -name "*.csv" -type f | wc -l)
        if [ $CSV_COUNT -gt 0 ]; then
            echo "  Found $CSV_COUNT CSV file(s):"
            find "$CSV_DIR" -maxdepth 1 -name "*.csv" -type f -exec basename {} \; | while read csv_name; do
                CSV_SIZE=$(du -h "$CSV_DIR/$csv_name" | cut -f1)
                echo "    • $csv_name ($CSV_SIZE)"
            done
        else
            print_info "No CSV files found (CSV import will be skipped)"
        fi
    else
        print_info "Directory NOT found (CSV import will be skipped)"
    fi
    echo ""
    
    print_config "Database Configuration:"
    echo "  Host:           $DB_IP"
    echo "  Port:           $DB_PORT"
    echo "  Database:       $DB_NAME"
    echo "  Container:      $CONTAINER_NAME"
    echo ""
    
    read -p "Continue with these settings? (Y/n): " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Nn]$ ]]; then
        print_info "Setup cancelled by user"
        print_info "Edit the paths at the top of this script if needed"
        exit 0
    fi
}

###############################################################################
# Pre-flight Checks
###############################################################################

check_prerequisites() {
    print_header "Checking Prerequisites"
    
    # Check if Docker is installed
    if ! command -v docker &> /dev/null; then
        print_error "Docker is not installed"
        print_info "Please run './setup-docker-postgres.sh' first"
        exit 1
    fi
    print_success "Docker is installed"
    
    # Check if PostgreSQL container exists
    if ! docker ps -a --format '{{.Names}}' | grep -q "^${CONTAINER_NAME}$"; then
        print_error "PostgreSQL container '$CONTAINER_NAME' does not exist"
        print_info "Please run './setup-docker-postgres.sh' first"
        exit 1
    fi
    print_success "PostgreSQL container exists"
    
    # Check if container is running
    if ! docker ps --format '{{.Names}}' | grep -q "^${CONTAINER_NAME}$"; then
        print_info "PostgreSQL container is not running. Starting it..."
        docker start $CONTAINER_NAME
        sleep 5
    fi
    print_success "PostgreSQL container is running"
    
    # Check database connectivity
    if ! docker exec $CONTAINER_NAME psql -U $DB_USER -d $DB_NAME -c "SELECT 1;" &> /dev/null; then
        print_error "Cannot connect to database"
        exit 1
    fi
    print_success "Database connection successful"
}

###############################################################################
# Database Schema Setup
###############################################################################

setup_database_schema() {
    print_header "Database Schema Setup"
    
    # Check if PostGIS is installed
    print_info "Verifying PostGIS extension..."
    docker exec $CONTAINER_NAME psql -U $DB_USER -d $DB_NAME -c "CREATE EXTENSION IF NOT EXISTS postgis;"
    
    POSTGIS_VERSION=$(docker exec $CONTAINER_NAME psql -U $DB_USER -d $DB_NAME -tAc "SELECT postgis_version();")
    print_success "PostGIS is installed: $POSTGIS_VERSION"
    echo ""
    
    # Execute SQL files in order
    for sql_file in "${SQL_FILES[@]}"; do
        SQL_PATH="${SQL_DIR}/${sql_file}"
        
        if [ -f "$SQL_PATH" ]; then
            print_info "Executing $sql_file..."
            
            # Copy SQL file to container
            docker cp "$SQL_PATH" ${CONTAINER_NAME}:/tmp/${sql_file}
            
            # Execute SQL file
            if docker exec $CONTAINER_NAME psql -U $DB_USER -d $DB_NAME -f /tmp/${sql_file} 2>&1 | tee /tmp/sql_output.log; then
                print_success "Successfully executed $sql_file"
            else
                print_error "Failed to execute $sql_file"
                print_info "Check /tmp/sql_output.log for details"
                # Continue with other files
            fi
            
            # Clean up
            docker exec $CONTAINER_NAME rm /tmp/${sql_file}
            echo ""
        else
            print_error "SQL file not found: $SQL_PATH"
        fi
    done
}

###############################################################################
# CSV Data Import
###############################################################################

import_csv_data() {
    print_header "CSV Data Import"
    
    # Check if CSV directory exists
    if [ ! -d "$CSV_DIR" ]; then
        print_info "CSV directory not found, skipping data import"
        print_info "Expected location: $CSV_DIR"
        return 0
    fi
    
    # Find all CSV files
    CSV_FILES=($(find "$CSV_DIR" -maxdepth 1 -name "*.csv" -type f))
    
    if [ ${#CSV_FILES[@]} -eq 0 ]; then
        print_info "No CSV files found in $CSV_DIR"
        print_info "Skipping CSV import"
        return 0
    fi
    
    print_info "Found ${#CSV_FILES[@]} CSV file(s) in the directory:"
    for csv_file in "${CSV_FILES[@]}"; do
        CSV_NAME=$(basename "$csv_file")
        CSV_SIZE=$(du -h "$csv_file" | cut -f1)
        echo "  • $CSV_NAME ($CSV_SIZE)"
    done
    echo ""
    
    read -p "Do you want to import CSV data into accident_reports table? (y/N): " -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        print_info "Skipping CSV import"
        return 0
    fi
    
    echo ""
    
    # Get initial record count
    INITIAL_COUNT=$(docker exec $CONTAINER_NAME psql -U $DB_USER -d $DB_NAME -tAc "SELECT COUNT(*) FROM public.accident_reports;" 2>/dev/null || echo "0")
    print_info "Initial records in accident_reports: $INITIAL_COUNT"
    echo ""
    
    # Import each CSV file
    local total_imported=0
    local files_imported=0
    local files_failed=0
    
    for csv_file in "${CSV_FILES[@]}"; do
        CSV_NAME=$(basename "$csv_file")
        CSV_SIZE=$(du -h "$csv_file" | cut -f1)
        
        print_info "Processing: $CSV_NAME ($CSV_SIZE)..."
        
        # Copy CSV to container
        CONTAINER_CSV_PATH="/tmp/${CSV_NAME}"
        if docker cp "$csv_file" ${CONTAINER_NAME}:${CONTAINER_CSV_PATH}; then
            
            # Set permissions
            docker exec $CONTAINER_NAME chmod 644 ${CONTAINER_CSV_PATH}
            
            # Get record count before import for this file
            BEFORE_COUNT=$(docker exec $CONTAINER_NAME psql -U $DB_USER -d $DB_NAME -tAc "SELECT COUNT(*) FROM public.accident_reports;")
            
            # Import CSV
            if docker exec $CONTAINER_NAME psql -U $DB_USER -d $DB_NAME -c "
            COPY public.accident_reports (
                latitude, longitude, accident_location, gis_coordinates,
                user_id, num_affecties, age, created_at, status,
                image_uri, audio_uri, video_uri, description,
                officer_name, officer_designation, officer_contact_no, officer_notes,
                weather_condition, visibility, road_surface_condition, road_type,
                road_markings, preliminary_fault, gender, cause, vehicle_involved_id,
                patient_victim_id, accident_type_id, severity
            )
            FROM '${CONTAINER_CSV_PATH}'
            DELIMITER ',' CSV HEADER;
            " &> /dev/null; then
                # Get record count after import
                AFTER_COUNT=$(docker exec $CONTAINER_NAME psql -U $DB_USER -d $DB_NAME -tAc "SELECT COUNT(*) FROM public.accident_reports;")
                IMPORTED=$((AFTER_COUNT - BEFORE_COUNT))
                total_imported=$((total_imported + IMPORTED))
                files_imported=$((files_imported + 1))
                print_success "Imported $IMPORTED records from $CSV_NAME"
            else
                print_error "Failed to import $CSV_NAME (check CSV format and column mapping)"
                files_failed=$((files_failed + 1))
            fi
            
            # Clean up
            docker exec $CONTAINER_NAME rm ${CONTAINER_CSV_PATH} &> /dev/null
        else
            print_error "Failed to copy $CSV_NAME to container"
            files_failed=$((files_failed + 1))
        fi
        
        echo ""
    done
    
    # Final summary
    print_header "CSV Import Summary"
    echo "  Total CSV files found:    ${#CSV_FILES[@]}"
    echo "  Successfully imported:    $files_imported"
    echo "  Failed:                   $files_failed"
    echo "  Total records imported:   $total_imported"
    echo ""
    
    FINAL_COUNT=$(docker exec $CONTAINER_NAME psql -U $DB_USER -d $DB_NAME -tAc "SELECT COUNT(*) FROM public.accident_reports;")
    print_success "Total records in accident_reports: $FINAL_COUNT"
}

###############################################################################
# Verification
###############################################################################

verify_database() {
    print_header "Verifying Database Setup"
    
    # Count tables
    TABLE_COUNT=$(docker exec $CONTAINER_NAME psql -U $DB_USER -d $DB_NAME -tAc "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = 'public';")
    print_success "Total tables created: $TABLE_COUNT"
    
    # List some key tables
    print_info "Key tables:"
    docker exec $CONTAINER_NAME psql -U $DB_USER -d $DB_NAME -c "
    SELECT table_name 
    FROM information_schema.tables 
    WHERE table_schema = 'public' 
    ORDER BY table_name;
    " | head -20
    
    echo ""
    print_info "Database is ready for use"
}

###############################################################################
# Main Execution
###############################################################################

main() {
    print_header "Database Schema & Data Import Script"
    
    # Verify paths and show configuration
    verify_paths
    
    # Check prerequisites
    check_prerequisites
    
    # Setup database schema
    setup_database_schema
    
    # Import CSV data (optional)
    import_csv_data
    
    # Verify database
    verify_database
    
    print_header "Database Setup Complete!"
    print_success "Your database is ready for the IRS application"
    print_info "Connection Details:"
    echo "  JDBC URL: jdbc:postgresql://localhost:${DB_PORT}/${DB_NAME}"
    echo "  Username: ${DB_USER}"
    echo ""
    print_info "You can now start your Quarkus application:"
    echo "  ./mvnw quarkus:dev"
    echo ""
}

# Run main function
main
