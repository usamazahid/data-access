#!/bin/bash

###############################################################################
# Database Schema & Data Import Script (Universal Version)
# This script automatically detects:
# - PostgreSQL in Docker container
# - PostgreSQL installed locally
# And uses the appropriate connection method
#
# Prerequisites: PostgreSQL running (Docker or Local)
###############################################################################

set -e  # Exit on any error

# Color codes for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
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

# Database Configuration
DB_HOST=${DB_HOST:-localhost}
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

# Connection type (will be auto-detected)
CONNECTION_TYPE=""  # "docker" or "local"

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

print_detect() {
    echo -e "${CYAN}🔍 $1${NC}"
}

print_header() {
    echo ""
    echo "═══════════════════════════════════════════════════════════════"
    echo "  $1"
    echo "═══════════════════════════════════════════════════════════════"
    echo ""
}

###############################################################################
# PostgreSQL Detection
###############################################################################

detect_postgres_connection() {
    print_header "Detecting PostgreSQL Connection"
    
    # Check 1: Docker container
    print_detect "Checking for PostgreSQL in Docker container..."
    if command -v docker &> /dev/null; then
        if docker ps --format '{{.Names}}' | grep -q "^${CONTAINER_NAME}$"; then
            print_success "Found PostgreSQL running in Docker container: $CONTAINER_NAME"
            CONNECTION_TYPE="docker"
            return 0
        else
            print_info "Docker is installed but container '$CONTAINER_NAME' is not running"
        fi
    else
        print_info "Docker is not installed"
    fi
    
    # Check 2: Local PostgreSQL
    print_detect "Checking for local PostgreSQL installation..."
    if command -v psql &> /dev/null; then
        # Try to connect to local PostgreSQL
        export PGPASSWORD="$DB_PASS"
        if psql -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USER" -d postgres -c "SELECT 1;" &> /dev/null; then
            print_success "Found PostgreSQL running locally"
            CONNECTION_TYPE="local"
            
            # Check if database exists
            DB_EXISTS=$(psql -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USER" -d postgres -tAc "SELECT 1 FROM pg_database WHERE datname='$DB_NAME';" 2>/dev/null)
            if [ "$DB_EXISTS" != "1" ]; then
                print_info "Database '$DB_NAME' does not exist. Creating it..."
                psql -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USER" -d postgres -c "CREATE DATABASE $DB_NAME;"
                print_success "Database '$DB_NAME' created"
            fi
            
            return 0
        else
            print_info "PostgreSQL is installed but cannot connect with provided credentials"
        fi
    else
        print_info "psql command not found (PostgreSQL client not installed)"
    fi
    
    # Check 3: Remote PostgreSQL (try without psql)
    print_detect "Checking for remote PostgreSQL..."
    if command -v nc &> /dev/null || command -v telnet &> /dev/null; then
        if nc -z "$DB_HOST" "$DB_PORT" 2>/dev/null || timeout 2 telnet "$DB_HOST" "$DB_PORT" 2>/dev/null | grep -q "Connected"; then
            print_info "PostgreSQL service detected on $DB_HOST:$DB_PORT but cannot verify access"
            print_error "Please install postgresql-client: sudo apt-get install postgresql-client"
            exit 1
        fi
    fi
    
    # Nothing found
    print_error "No PostgreSQL instance found!"
    echo ""
    echo "Please ensure one of the following:"
    echo "  1. PostgreSQL is installed locally and running"
    echo "  2. PostgreSQL Docker container is running"
    echo "  3. Run './setup-docker-postgres.sh' to set up PostgreSQL in Docker"
    echo ""
    exit 1
}

###############################################################################
# Execute SQL Command (Universal)
###############################################################################

execute_sql() {
    local sql_command="$1"
    
    if [ "$CONNECTION_TYPE" == "docker" ]; then
        docker exec $CONTAINER_NAME psql -U $DB_USER -d $DB_NAME -c "$sql_command"
    elif [ "$CONNECTION_TYPE" == "local" ]; then
        export PGPASSWORD="$DB_PASS"
        psql -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USER" -d "$DB_NAME" -c "$sql_command"
    fi
}

execute_sql_quiet() {
    local sql_command="$1"
    
    if [ "$CONNECTION_TYPE" == "docker" ]; then
        docker exec $CONTAINER_NAME psql -U $DB_USER -d $DB_NAME -tAc "$sql_command"
    elif [ "$CONNECTION_TYPE" == "local" ]; then
        export PGPASSWORD="$DB_PASS"
        psql -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USER" -d "$DB_NAME" -tAc "$sql_command"
    fi
}

execute_sql_file() {
    local sql_file="$1"
    
    if [ "$CONNECTION_TYPE" == "docker" ]; then
        docker exec $CONTAINER_NAME psql -U $DB_USER -d $DB_NAME -f "$sql_file"
    elif [ "$CONNECTION_TYPE" == "local" ]; then
        export PGPASSWORD="$DB_PASS"
        psql -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USER" -d "$DB_NAME" -f "$sql_file"
    fi
}

copy_file_to_db_server() {
    local source_file="$1"
    local dest_path="$2"
    
    if [ "$CONNECTION_TYPE" == "docker" ]; then
        docker cp "$source_file" ${CONTAINER_NAME}:"$dest_path"
    elif [ "$CONNECTION_TYPE" == "local" ]; then
        # For local PostgreSQL, copy to /tmp (accessible by postgres user)
        sudo cp "$source_file" "$dest_path"
        sudo chmod 644 "$dest_path"
        sudo chown postgres:postgres "$dest_path" 2>/dev/null || true
    fi
}

remove_file_from_db_server() {
    local file_path="$1"
    
    if [ "$CONNECTION_TYPE" == "docker" ]; then
        docker exec $CONTAINER_NAME rm "$file_path" &> /dev/null || true
    elif [ "$CONNECTION_TYPE" == "local" ]; then
        sudo rm "$file_path" &> /dev/null || true
    fi
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
        CSV_COUNT=$(find "$CSV_DIR" -maxdepth 1 -name "*.csv" -type f 2>/dev/null | wc -l)
        if [ $CSV_COUNT -gt 0 ]; then
            echo "  Found $CSV_COUNT CSV file(s):"
            find "$CSV_DIR" -maxdepth 1 -name "*.csv" -type f -exec basename {} \; | while read csv_name; do
                CSV_SIZE=$(du -h "$CSV_DIR/$csv_name" 2>/dev/null | cut -f1)
                echo "    • $csv_name ($CSV_SIZE)"
            done
        else
            print_info "No CSV files found (CSV import will be skipped)"
        fi
    else
        print_info "Directory NOT found (CSV import will be skipped)"
    fi
    echo ""
    
    print_config "PostgreSQL Connection:"
    echo "  Type:           $CONNECTION_TYPE"
    if [ "$CONNECTION_TYPE" == "docker" ]; then
        echo "  Container:      $CONTAINER_NAME"
    else
        echo "  Host:           $DB_HOST"
        echo "  Port:           $DB_PORT"
    fi
    echo "  Database:       $DB_NAME"
    echo "  User:           $DB_USER"
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
# Database Schema Setup
###############################################################################

setup_database_schema() {
    print_header "Database Schema Setup"
    
    # Check if PostGIS is installed
    print_info "Verifying PostGIS extension..."
    execute_sql "CREATE EXTENSION IF NOT EXISTS postgis;"
    
    POSTGIS_VERSION=$(execute_sql_quiet "SELECT postgis_version();")
    print_success "PostGIS is installed: $POSTGIS_VERSION"
    echo ""
    
    # Execute SQL files in order
    for sql_file in "${SQL_FILES[@]}"; do
        SQL_PATH="${SQL_DIR}/${sql_file}"
        
        if [ -f "$SQL_PATH" ]; then
            print_info "Executing $sql_file..."
            
            if [ "$CONNECTION_TYPE" == "docker" ]; then
                # Copy SQL file to container and execute
                docker cp "$SQL_PATH" ${CONTAINER_NAME}:/tmp/${sql_file}
                if execute_sql_file /tmp/${sql_file} 2>&1 | tee /tmp/sql_output.log; then
                    print_success "Successfully executed $sql_file"
                else
                    print_error "Failed to execute $sql_file"
                fi
                docker exec $CONTAINER_NAME rm /tmp/${sql_file} &> /dev/null || true
            elif [ "$CONNECTION_TYPE" == "local" ]; then
                # Execute SQL file directly
                export PGPASSWORD="$DB_PASS"
                if psql -h "$DB_HOST" -p "$DB_PORT" -U "$DB_USER" -d "$DB_NAME" -f "$SQL_PATH" 2>&1 | tee /tmp/sql_output.log; then
                    print_success "Successfully executed $sql_file"
                else
                    print_error "Failed to execute $sql_file"
                fi
            fi
            
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
        return 0
    fi
    
    # Find all CSV files
    CSV_FILES=($(find "$CSV_DIR" -maxdepth 1 -name "*.csv" -type f 2>/dev/null))
    
    if [ ${#CSV_FILES[@]} -eq 0 ]; then
        print_info "No CSV files found in $CSV_DIR"
        return 0
    fi
    
    print_info "Found ${#CSV_FILES[@]} CSV file(s):"
    for csv_file in "${CSV_FILES[@]}"; do
        CSV_NAME=$(basename "$csv_file")
        CSV_SIZE=$(du -h "$csv_file" 2>/dev/null | cut -f1)
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
    INITIAL_COUNT=$(execute_sql_quiet "SELECT COUNT(*) FROM public.accident_reports;" 2>/dev/null || echo "0")
    print_info "Initial records in accident_reports: $INITIAL_COUNT"
    echo ""
    
    # Import each CSV file
    local total_imported=0
    local files_imported=0
    local files_failed=0
    
    for csv_file in "${CSV_FILES[@]}"; do
        CSV_NAME=$(basename "$csv_file")
        CSV_SIZE=$(du -h "$csv_file" 2>/dev/null | cut -f1)
        
        print_info "Processing: $CSV_NAME ($CSV_SIZE)..."
        
        CONTAINER_CSV_PATH="/tmp/${CSV_NAME}"
        
        # Copy CSV to appropriate location
        if copy_file_to_db_server "$csv_file" "$CONTAINER_CSV_PATH" 2>&1; then
            
            # Get record count before import
            BEFORE_COUNT=$(execute_sql_quiet "SELECT COUNT(*) FROM public.accident_reports;")
            
            # Import CSV
            COPY_SQL="COPY public.accident_reports (
                latitude, longitude, accident_location, gis_coordinates,
                user_id, num_affecties, age, created_at, status,
                image_uri, audio_uri, video_uri, description,
                officer_name, officer_designation, officer_contact_no, officer_notes,
                weather_condition, visibility, road_surface_condition, road_type,
                road_markings, preliminary_fault, gender, cause, vehicle_involved_id,
                patient_victim_id, accident_type_id, severity
            )
            FROM '${CONTAINER_CSV_PATH}'
            DELIMITER ',' CSV HEADER;"
            
            if execute_sql "$COPY_SQL" &> /dev/null; then
                # Get record count after import
                AFTER_COUNT=$(execute_sql_quiet "SELECT COUNT(*) FROM public.accident_reports;")
                IMPORTED=$((AFTER_COUNT - BEFORE_COUNT))
                total_imported=$((total_imported + IMPORTED))
                files_imported=$((files_imported + 1))
                print_success "Imported $IMPORTED records from $CSV_NAME"
            else
                print_error "Failed to import $CSV_NAME (check CSV format)"
                files_failed=$((files_failed + 1))
            fi
            
            # Clean up
            remove_file_from_db_server "$CONTAINER_CSV_PATH"
        else
            print_error "Failed to copy $CSV_NAME"
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
    
    FINAL_COUNT=$(execute_sql_quiet "SELECT COUNT(*) FROM public.accident_reports;")
    print_success "Total records in accident_reports: $FINAL_COUNT"
}

###############################################################################
# Verification
###############################################################################

verify_database() {
    print_header "Verifying Database Setup"
    
    # Count tables
    TABLE_COUNT=$(execute_sql_quiet "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = 'public';")
    print_success "Total tables created: $TABLE_COUNT"
    
    echo ""
    print_info "Database is ready for use"
}

###############################################################################
# Main Execution
###############################################################################

main() {
    print_header "Database Schema & Data Import Script (Universal)"
    
    # Detect PostgreSQL connection type
    detect_postgres_connection
    
    echo ""
    
    # Verify paths and show configuration
    verify_paths
    
    # Setup database schema
    setup_database_schema
    
    # Import CSV data (optional)
    import_csv_data
    
    # Verify database
    verify_database
    
    print_header "Database Setup Complete!"
    print_success "Your database is ready for the IRS application"
    print_info "Connection Details:"
    if [ "$CONNECTION_TYPE" == "docker" ]; then
        echo "  Type:     Docker Container"
        echo "  JDBC URL: jdbc:postgresql://localhost:${DB_PORT}/${DB_NAME}"
    else
        echo "  Type:     Local PostgreSQL"
        echo "  JDBC URL: jdbc:postgresql://${DB_HOST}:${DB_PORT}/${DB_NAME}"
    fi
    echo "  Username: ${DB_USER}"
    echo ""
    print_info "You can now start your Quarkus application:"
    echo "  ./mvnw quarkus:dev"
    echo ""
}

# Run main function
main
