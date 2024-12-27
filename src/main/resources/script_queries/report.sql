

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
	status varchar(50) DEFAULT 'pending'::character varying NULL,
	description text NULL,
	created_at timestamp DEFAULT CURRENT_TIMESTAMP NULL,
	CONSTRAINT accident_reports_gender_check CHECK (((gender)::text = ANY (ARRAY[('male'::character varying)::text, ('female'::character varying)::text, ('other'::character varying)::text]))),
	CONSTRAINT accident_reports_pkey PRIMARY KEY (report_id),
	CONSTRAINT accident_reports_accident_type_id_fkey FOREIGN KEY (accident_type_id) REFERENCES public.accident_types(id),
	CONSTRAINT accident_reports_patient_victim_id_fkey FOREIGN KEY (patient_victim_id) REFERENCES public.patient_victim(id),
	CONSTRAINT accident_reports_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id),
	CONSTRAINT accident_reports_vehicle_involved_id_fkey FOREIGN KEY (vehicle_involved_id) REFERENCES public.vehicle_involved(id)
);



CREATE TABLE IF NOT EXISTS dispatch (
    dispatch_id SERIAL PRIMARY KEY,
    report_id INT REFERENCES accident_reports(report_id) ON DELETE CASCADE,
    ambulance_id INT REFERENCES ambulance(ambulance_id),
    driver_id INT REFERENCES users(user_id),
    assigned_by INT REFERENCES users(user_id),
    pickup_time TIMESTAMP,
    drop_time TIMESTAMP,
    latitude numeric(9, 6) NULL,
	longitude numeric(9, 6) NULL,
	drop_location varchar(255) NULL,
    hospital_id INT REFERENCES hospital(hospital_id),
    status VARCHAR(50) DEFAULT 'in progress' -- in progress, pending, assigned,droped,picked
); 


