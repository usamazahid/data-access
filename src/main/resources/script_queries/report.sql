

--postgrel extension of postgis
--step1: sudo apt install postgis postgresql-17-postgis-3
--step2: sudo systemctl restart postgresql
--step3: SELECT * FROM pg_available_extensions WHERE name = 'postgis';
--step4: 
CREATE EXTENSION postgis;
SELECT postgis_version();


--drop table accident_reports;



CREATE TABLE public.accident_reports (
	report_id serial4 NOT NULL,
	latitude numeric(9, 6) NULL,
	longitude numeric(9, 6) NULL,
	accident_location varchar(255) NULL,
    gis_coordinates GEOMETRY(Point, 4326) NULL,
	user_id int4 NULL,
	num_affecties int4 NULL,
	age int4 NULL,
	created_at timestamp DEFAULT CURRENT_TIMESTAMP NULL,
	status varchar(50) DEFAULT 'pending'::character varying NULL,
    severity int4 DEFAULT 1 NULL,
    image_uri text NULL,
	audio_uri text NULL,
    video_uri text NULL,
	description text NULL,
    officer_name VARCHAR(255),
    officer_designation VARCHAR(100),
    officer_contact_no VARCHAR(20),
    officer_notes TEXT,
    weather_condition  int4 NULL,
    visibility  int4 NULL,
    road_surface_condition  int4 NULL,
    road_type  int4 NULL,
    road_markings  int4 NULL,
    preliminary_fault  int4 NULL,
	gender  int4 NULL,
    cause  int4 NULL,
    vehicle_involved_id int4 NULL,
	patient_victim_id int4 NULL,
	accident_type_id int4 NULL,
	CONSTRAINT pk_accident_reports_report_id PRIMARY KEY (report_id),
	CONSTRAINT fk_accident_reports_accident_type_id FOREIGN KEY (accident_type_id) REFERENCES public.accident_types(id),
	CONSTRAINT fk_accident_reports_patient_victim_id FOREIGN KEY (patient_victim_id) REFERENCES public.patient_victim(id),
	CONSTRAINT fk_accident_reports_user_id FOREIGN KEY (user_id) REFERENCES public.users(id),
	CONSTRAINT fk_accident_reports_vehicle_involved_id FOREIGN KEY (vehicle_involved_id) REFERENCES public.vehicle_involved(id),
    CONSTRAINT fk_accident_reports_weather FOREIGN KEY (weather_condition) REFERENCES weather_condition(id),
    CONSTRAINT fk_accident_reports_visibility FOREIGN KEY (visibility) REFERENCES visibility(id),
    CONSTRAINT fk_accident_reports_road_surface FOREIGN KEY (road_surface_condition) REFERENCES road_surface_condition(id),
    CONSTRAINT fk_accident_reports_road_type FOREIGN KEY (road_type) REFERENCES road_type(id),
    CONSTRAINT fk_accident_reports_road_markings FOREIGN KEY (road_markings) REFERENCES road_signage(id),
    CONSTRAINT fk_accident_reports_preliminary_fault FOREIGN KEY (preliminary_fault) REFERENCES preliminary_fault_assessment(id),
    CONSTRAINT fk_accident_reports_gender_type FOREIGN KEY (gender) REFERENCES gender_types(id),
    CONSTRAINT fk_accident_reports_apparent_cause FOREIGN KEY (cause) REFERENCES apparent_cause(id)
);


-- Convert existing latitude & longitude into the PostGIS geometry format
UPDATE accident_reports 
SET gis_coordinates = ST_SetSRID(ST_MakePoint(longitude, latitude), 4326);

CREATE INDEX accidents_location_gist ON accident_reports USING GIST(gis_coordinates);

--drop table accident_report_images ;

-- Separate table for image URIs
CREATE TABLE public.accident_report_images (
    image_id SERIAL PRIMARY KEY,
    report_id INT NOT NULL,
    image_uri TEXT NOT NULL,
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    -- Foreign Key linking to accident_reports
    CONSTRAINT accident_report_images_report_id_fkey FOREIGN KEY (report_id) REFERENCES public.accident_reports(report_id) ON DELETE CASCADE
);


--drop table "dispatch" ;

CREATE TABLE IF NOT EXISTS dispatch (
    dispatch_id SERIAL PRIMARY KEY,
    report_id INT REFERENCES accident_reports(report_id) ON DELETE CASCADE,
    ambulance_id INT REFERENCES ambulance(ambulance_id),
    driver_id INT REFERENCES users(id),
    assigned_by INT REFERENCES users(id),
    pickup_time TIMESTAMP,
    drop_time TIMESTAMP,
    latitude numeric(9, 6) NULL,
	longitude numeric(9, 6) NULL,
	drop_location varchar(255) NULL,
    hospital_id INT REFERENCES organizations(id),
    status VARCHAR(50) DEFAULT 'in progress' -- in progress, pending, assigned,droped,picked
); 



--drop table vehicle_details ;

-- Vehicle Details
CREATE TABLE IF NOT EXISTS vehicle_details  (
    id SERIAL PRIMARY KEY,
    report_id INT REFERENCES accident_reports(report_id) ON DELETE CASCADE,
    registration_no VARCHAR(50),
    type int4 NULL,
    condition VARCHAR(50),
    fitness_certificate_status VARCHAR(50),
    road_tax_status int4 NULL,
    insurance_status int4 NULL,
    CONSTRAINT fk_vehicle_details_vechile_type_id FOREIGN KEY (type) REFERENCES public.vehicle_involved(id) ON DELETE CASCADE,
    CONSTRAINT fk_vehicle_details_insurance_status FOREIGN KEY (insurance_status) REFERENCES public.insurance_status(id) ON DELETE CASCADE,
    CONSTRAINT fk_vehicle_details_road_tax_status FOREIGN KEY (road_tax_status) REFERENCES public.road_tax_status(id) ON DELETE CASCADE

);

--drop table driver_details ;

-- Driver Details
CREATE TABLE IF NOT EXISTS driver_details (
    id SERIAL PRIMARY KEY,
    report_id INT REFERENCES accident_reports(report_id) ON DELETE CASCADE,
    name VARCHAR(255),
    cnic_no VARCHAR(15),
    license_no VARCHAR(50),
    contact_no VARCHAR(20)
);

--drop table passenger_casualties ;

-- Passenger & Casualties
CREATE TABLE IF NOT EXISTS passenger_casualties (
    id SERIAL PRIMARY KEY,
    report_id INT REFERENCES accident_reports(report_id) ON DELETE CASCADE,
    type int4 NULL,
    name VARCHAR(255),
    hospital_name VARCHAR(255),
    injury_severity int4 NULL,
    CONSTRAINT fk_passenger_casualties_injury_severity FOREIGN KEY (injury_severity) REFERENCES injury_severity(id),
    CONSTRAINT fk_passenger_casualties_causalities_status FOREIGN KEY (type) REFERENCES causalities_status(id)
);

--drop table evidence_collection;

-- Evidence Collection
CREATE TABLE IF NOT EXISTS evidence_collection (
    id SERIAL PRIMARY KEY,
    report_id INT REFERENCES accident_reports(report_id) ON DELETE CASCADE,
    photos BOOLEAN,
    videos BOOLEAN,
    sketch BOOLEAN
);

--drop table witness_details;

-- Witness Details
CREATE TABLE IF NOT EXISTS witness_details (
    id SERIAL PRIMARY KEY,
    report_id INT REFERENCES accident_reports(report_id) ON DELETE CASCADE,
    name VARCHAR(255),
    contact_no VARCHAR(20),
    address TEXT
);

--drop table follow_up_actions;

-- Follow-Up Actions
CREATE TABLE IF NOT EXISTS follow_up_actions (
    id SERIAL PRIMARY KEY,
    report_id INT REFERENCES accident_reports(report_id) ON DELETE CASCADE,
    fir_registered BOOLEAN,
    fir_number VARCHAR(50),
    challan_issued BOOLEAN,
    challan_number VARCHAR(50),
    case_referred_to int4 NULL,
    CONSTRAINT fk_follow_up_actions_case_referred FOREIGN KEY (case_referred_to) REFERENCES case_referred_to(id)
);

-- 🚀 SQL Script for Vehicle Fitness & Document Verification

--drop table accident_vehicle_fitness;

CREATE TABLE public.accident_vehicle_fitness (
    fitness_id SERIAL PRIMARY KEY,
    report_id INT NOT NULL,
    vehicle_no VARCHAR(50) NOT NULL,
    fitness_certificate_valid BOOLEAN NOT NULL,
    expiry_date DATE,
    road_tax_status int4 NULL,
    insurance_status int4 NULL,

    -- Foreign Key linking to accident_reports
    CONSTRAINT accident_vehicle_fitness_report_id_fkey FOREIGN KEY (report_id) REFERENCES public.accident_reports(report_id) ON DELETE CASCADE,
    CONSTRAINT fk_accident_vehicle_fitness_road_tax_status FOREIGN KEY (road_tax_status) REFERENCES road_tax_status(id),
    CONSTRAINT fk_accident_vehicle_fitness_insurance_status FOREIGN KEY (insurance_status) REFERENCES public.insurance_status(id) ON DELETE CASCADE

);

