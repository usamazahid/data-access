
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
