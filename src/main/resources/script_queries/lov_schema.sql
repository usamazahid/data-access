
---deleting
DROP TABLE IF EXISTS accident_types CASCADE; 
DROP TABLE IF EXISTS patient_victim CASCADE; 
DROP TABLE IF EXISTS vehicle_involved CASCADE; 
DROP TABLE IF EXISTS organizations CASCADE;  
DROP TABLE IF EXISTS user_organizations CASCADE;  

-- Create accident_types table if it doesn't exist
CREATE TABLE IF NOT EXISTS public.accident_types (
    id SERIAL PRIMARY KEY,
    label VARCHAR NOT NULL UNIQUE,
    description VARCHAR NULL
);

-- Insert data into accident_types table, ignoring duplicates
INSERT INTO public.accident_types (label, description)
VALUES
    ('Minor Collision', 'A minor road accident with no serious injuries.'),
    ('Major Collision', 'A major road accident involving serious damage or injuries.'),
    ('Vehicle Rollover', 'An accident where a vehicle tips over onto its side or roof.'),
    ('Hit and Run', 'An accident where the vehicle involved leaves the scene without stopping.'),
    ('Pedestrian Accident', 'An accident involving a vehicle and a pedestrian.')
ON CONFLICT (label) DO NOTHING;

-- Create organizations table if it doesn't exist
CREATE TABLE IF NOT EXISTS public.organizations (
    id SERIAL PRIMARY KEY,
    label VARCHAR NOT NULL UNIQUE,
    image_uri VARCHAR NULL,
    description TEXT NULL,
    phone VARCHAR NULL,
    location TEXT NULL,
    has_ambulance_service BOOLEAN DEFAULT TRUE,
    is_hospital BOOLEAN DEFAULT FALSE
);



CREATE TABLE IF NOT EXISTS user_organizations (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL,
    organization_id INT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (organization_id) REFERENCES organizations(id)
);


-- Insert data into organizations table, ignoring duplicates
INSERT INTO public.organizations (label, image_uri, description, phone)
VALUES
    ('Rescue 1122', 'https://raw.githubusercontent.com/usamazahid/IRS/main/src/assets/img/rescue_1122.jpg', 'Emergency rescue service providing rapid response.', '+9201234567'),
    ('Edhi Foundation', 'https://raw.githubusercontent.com/usamazahid/IRS/main/src/assets/img/edhi_ambulance.jpg', 'A charitable organization offering emergency services.', '+9207654321'),
    ('Chhipa', 'https://raw.githubusercontent.com/usamazahid/IRS/main/src/assets/img/ambulance_icon.jpg', 'Provides ambulance and emergency services.', '+9201122334'),
    ('Suhayl Ambulance Service', 'https://raw.githubusercontent.com/usamazahid/IRS/main/src/assets/img/ambulance_icon.jpg', 'Offers medical emergency and ambulance services.', '+9205566778'),
    ('Gulab Devi Hospital Ambulance', 'https://raw.githubusercontent.com/usamazahid/IRS/main/src/assets/img/ambulance_icon.jpg', 'Hospital ambulance service providing critical care.', '+9209988776')
ON CONFLICT (label) DO NOTHING;

-- Create vehicle_involved table if it doesn't exist
CREATE TABLE IF NOT EXISTS public.vehicle_involved (
    id SERIAL PRIMARY KEY,
    label VARCHAR NOT NULL UNIQUE,
    description TEXT NULL
);

-- Insert data into vehicle_involved table, ignoring duplicates
INSERT INTO public.vehicle_involved (label, description)
VALUES
    ('Pedestrian', 'A person who is walking, especially in an urban area.'),
    ('Bicycle', 'A human-powered vehicle with two wheels.'),
    ('Motorbike', 'A two-wheeled motor vehicle.'),
    ('Mini van Coaster', 'A small passenger vehicle.'),
    ('Bus/Mini Bus/Coach', 'A large motor vehicle for transporting passengers.'),
    ('Truck', 'A motor vehicle designed primarily for carrying cargo.'),
    ('Taxi', 'A car licensed to transport passengers in return for payment.'),
    ('Car', 'A road vehicle, typically with four wheels, powered by an internal combustion engine.'),
    ('Water Tanker', 'A truck designed to transport water.'),
    ('Rickshaw/Chinqchi', 'A small, lightweight vehicle powered by human or motor.'),
    ('Dumper', 'A truck designed for carrying bulk material.'),
    ('Trailer', 'A non-motorized vehicle designed to be towed.'),
    ('Loading Pickup', 'A vehicle designed for carrying cargo.'),
    ('Others', 'Any other type of vehicle not listed.')
ON CONFLICT (label) DO NOTHING;

-- Create patient_victim table if it doesn't exist
CREATE TABLE IF NOT EXISTS public.patient_victim (
    id SERIAL PRIMARY KEY,
    label VARCHAR NOT NULL UNIQUE,
    description TEXT NULL
);

-- Insert data into patient_victim table, ignoring duplicates
INSERT INTO public.patient_victim (label, description)
VALUES
    ('Rider', 'A person riding a motorcycle or similar vehicle.'),
    ('Pillion Rider', 'A passenger riding on the back of a motorcycle.'),
    ('Car/Taxi Driver', 'The person operating a car or taxi.'),
    ('Passenger', 'A person traveling in a vehicle but not operating it.'),
    ('Pedestrian', 'A person walking, especially near or on a road.'),
    ('Rickshaw/Chinqchi Driver', 'The person operating a rickshaw or chinqchi.'),
    ('Rickshaw/Chinqchi Passenger', 'A passenger traveling in a rickshaw or chinqchi.'),
    ('Others', 'Any other type of victim not listed.')
ON CONFLICT (label) DO NOTHING;


-- sql SELECT


--select * from accident_types at2 
--select * from vehicle_involved vi 
--select * from patient_victim pv 
--select * from organizations


--not yet decided

CREATE TABLE IF NOT EXISTS ambulance (
    ambulance_id SERIAL PRIMARY KEY,
    vehicle_number VARCHAR(50) UNIQUE NOT NULL,
    status VARCHAR(20) DEFAULT 'available', --assigned, available, not-available,in progress
    organizations_id INT REFERENCES organizations(id)
);


CREATE TABLE IF NOT EXISTS ambulance_drivers (
    id SERIAL PRIMARY KEY,
    driver_id INT REFERENCES users(id) ON DELETE CASCADE,
    ambulance_id INT REFERENCES ambulance(ambulance_id) ON DELETE CASCADE,
    start_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    end_date TIMESTAMP
);

-- Apparent Cause Table
CREATE TABLE apparent_cause (
    id SERIAL PRIMARY KEY,
    cause VARCHAR(50) NOT NULL,
    other_details VARCHAR(255)
);

INSERT INTO apparent_cause (cause, other_details) VALUES 
('Over speeding', NULL),
('Brake Failure', NULL),
('Road Condition', NULL),
('Driver Negligence', NULL),
('Weather Conditions', NULL),
('Mechanical Failure', NULL),
('Other', 'Driver fatigue');

-- Weather Condition Table
CREATE TABLE weather_condition (
    id SERIAL PRIMARY KEY,
    condition VARCHAR(50) NOT NULL
);

INSERT INTO weather_condition (condition) VALUES 
('Clear'),
('Rain'),
('Fog'),
('Dust'),
('Windy');

-- Visibility Table
CREATE TABLE visibility (
    id SERIAL PRIMARY KEY,
    level VARCHAR(50) NOT NULL
);

INSERT INTO visibility (level) VALUES 
('Good'),
('Moderate'),
('Poor');

-- Road Surface Condition Table
CREATE TABLE road_surface_condition (
    id SERIAL PRIMARY KEY,
    condition VARCHAR(50) NOT NULL
);

INSERT INTO road_surface_condition (condition) VALUES 
('Dry'),
('Wet'),
('Damaged'),
('Under Construction');

-- Road Type Table
CREATE TABLE road_type (
    id SERIAL PRIMARY KEY,
    type VARCHAR(50) NOT NULL
);

INSERT INTO road_type (type) VALUES 
('Highway'),
('Urban Road'),
('Intersection'),
('Service Road'),
('Bridge/Flyover');

-- Road Markings / Signage Table
CREATE TABLE road_signage (
    id SERIAL PRIMARY KEY,
    status VARCHAR(50) NOT NULL
);

INSERT INTO road_signage (status) VALUES 
('Clear'),
('Faded'),
('Missing');

-- Case Referred To Table
CREATE TABLE case_referred_to (
    id SERIAL PRIMARY KEY,
    unit VARCHAR(50) NOT NULL
);

INSERT INTO case_referred_to (unit) VALUES 
('Investigation Unit'),
('Traffic Police'),
('Legal Aid'),
('Not Applicable');

-- Preliminary Fault Assessment Table
CREATE TABLE preliminary_fault_assessment (
    id SERIAL PRIMARY KEY,
    fault VARCHAR(50) NOT NULL
);

INSERT INTO preliminary_fault_assessment (fault) VALUES 
('Driver 1'),
('Driver 2'),
('Road Condition'),
('Mechanical Failure'),
('Shared Fault'),
('Undetermined');

-- Let me know if you want me to add relationships or normalize the design further! 🚀
