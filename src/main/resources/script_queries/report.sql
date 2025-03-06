

CREATE TABLE public.accident_reports (
	report_id serial4 NOT NULL,
	latitude numeric(9, 6) NULL,
	longitude numeric(9, 6) NULL,
	accident_location varchar(255) NULL,
	vehicle_involved_id int4 NULL,
	patient_victim_id int4 NULL,
	accident_type_id int4 NULL,
	user_id int4 NULL,
	cause text NULL,
	num_affecties int4 NULL,
	age int4 NULL,
	gender varchar(10) NULL,
	image_uri text NULL,
	audio_uri text NULL,
    video_uri text NULL,
	status varchar(50) DEFAULT 'pending'::character varying NULL,
	description text NULL,
	weather_condition VARCHAR(50),
    visibility VARCHAR(50),
    road_surface_condition VARCHAR(50),
    road_type VARCHAR(50),
    road_markings VARCHAR(50),
    officer_name VARCHAR(255),
    officer_designation VARCHAR(100),
    officer_contact_no VARCHAR(20),
    preliminary_fault VARCHAR(50),
    officer_notes TEXT,
	created_at timestamp DEFAULT CURRENT_TIMESTAMP NULL,
	CONSTRAINT accident_reports_gender_check CHECK (((gender)::text = ANY (ARRAY[('male'::character varying)::text, ('female'::character varying)::text, ('other'::character varying)::text]))),
	CONSTRAINT accident_reports_pkey PRIMARY KEY (report_id),
	CONSTRAINT accident_reports_accident_type_id_fkey FOREIGN KEY (accident_type_id) REFERENCES public.accident_types(id),
	CONSTRAINT accident_reports_patient_victim_id_fkey FOREIGN KEY (patient_victim_id) REFERENCES public.patient_victim(id),
	CONSTRAINT accident_reports_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id),
	CONSTRAINT accident_reports_vehicle_involved_id_fkey FOREIGN KEY (vehicle_involved_id) REFERENCES public.vehicle_involved(id)
);


-- Separate table for image URIs
CREATE TABLE public.accident_report_images (
    image_id SERIAL PRIMARY KEY,
    report_id INT NOT NULL,
    image_uri TEXT NOT NULL,
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    -- Foreign Key linking to accident_reports
    CONSTRAINT accident_report_images_report_id_fkey FOREIGN KEY (report_id) REFERENCES public.accident_reports(report_id) ON DELETE CASCADE
);


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



-- 🚀 Database Schema Updates for Enhanced Accident Reporting System

-- Add new tables and update existing ones to capture detailed accident information

-- Vehicle Details
CREATE TABLE IF NOT EXISTS vehicle_details (
    id SERIAL PRIMARY KEY,
    report_id INT REFERENCES accident_reports(report_id) ON DELETE CASCADE,
    registration_no VARCHAR(50),
    type VARCHAR(50),
    condition VARCHAR(50),
    fitness_certificate_status VARCHAR(50),
    road_tax_status VARCHAR(50),
    insurance_status VARCHAR(50)
);

-- Driver Details
CREATE TABLE IF NOT EXISTS driver_details (
    id SERIAL PRIMARY KEY,
    report_id INT REFERENCES accident_reports(report_id) ON DELETE CASCADE,
    name VARCHAR(255),
    cnic_no VARCHAR(15),
    license_no VARCHAR(50),
    contact_no VARCHAR(20)
);

-- Passenger & Casualties
CREATE TABLE IF NOT EXISTS passenger_casualties (
    id SERIAL PRIMARY KEY,
    report_id INT REFERENCES accident_reports(report_id) ON DELETE CASCADE,
    type VARCHAR(50),
    name VARCHAR(255),
    hospital_name VARCHAR(255),
    injury_severity VARCHAR(50)
);

-- Evidence Collection
CREATE TABLE IF NOT EXISTS evidence_collection (
    id SERIAL PRIMARY KEY,
    report_id INT REFERENCES accident_reports(report_id) ON DELETE CASCADE,
    photos BOOLEAN,
    videos BOOLEAN,
    sketch BOOLEAN
);

-- Witness Details
CREATE TABLE IF NOT EXISTS witness_details (
    id SERIAL PRIMARY KEY,
    report_id INT REFERENCES accident_reports(report_id) ON DELETE CASCADE,
    name VARCHAR(255),
    contact_no VARCHAR(20),
    address TEXT
);

-- Follow-Up Actions
CREATE TABLE IF NOT EXISTS follow_up_actions (
    id SERIAL PRIMARY KEY,
    report_id INT REFERENCES accident_reports(report_id) ON DELETE CASCADE,
    fir_registered BOOLEAN,
    fir_number VARCHAR(50),
    challan_issued BOOLEAN,
    challan_number VARCHAR(50),
    case_referred_to VARCHAR(100)
);


-- 🚀 Ready to support detailed accident reporting with video and multiple images! Let me know if you want any adjustments or optimizations. ✨

