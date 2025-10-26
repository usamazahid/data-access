INSERT INTO public.users
  (mobile_number, full_name, username, password_hash, email,
   status, verification_status, created_at, device_token, device_id, designation)
SELECT
  -- mobile: 0300 followed by a random 7-digit number
  '0300' || lpad((1000000 + (s * 1234567) % 9000000)::text, 7, '0')    AS mobile_number,
  'Citizen ' || s                                                    AS full_name,
  'citizen' || s                                                     AS username,
  'test'                                                             AS password_hash,  -- replace with real hash if needed
  'citizen' || s || '@example.com'                                   AS email,
  'active'                                                           AS status,
  'N'                                                                AS verification_status,
  now()                                                              AS created_at,
  NULL                                                               AS device_token,
  NULL                                                               AS device_id,
  'citizen_auto'                                                          AS designation
FROM generate_series(1,50) AS s;

-- 2) Grant each newly created citizen the role_id = 2
--    We assume role_id 2 corresponds to “citizen”
INSERT INTO public.user_roles (user_id, role_id)
SELECT u.id, 2
FROM public.users u
WHERE u.designation = 'citizen_auto'
ON CONFLICT DO NOTHING;

---csv insertion_____
-- Note: CSV insertion is handled by the setup-database.sh script
-- The following is just documentation/example:
--
-- Steps to import CSV manually:
-- 1. Copy CSV file to container: docker cp file.csv postgres-irs:/tmp/file.csv
-- 2. Set permissions: docker exec postgres-irs chmod 644 /tmp/file.csv
-- 3. Run COPY command in psql
--
-- COPY public.accident_reports (
--   latitude, longitude, accident_location, gis_coordinates,
--   user_id, num_affecties, age, created_at, status,
--   image_uri, audio_uri, video_uri, description,
--   officer_name, officer_designation, officer_contact_no, officer_notes,
--   weather_condition, visibility, road_surface_condition, road_type,
--   road_markings, preliminary_fault, gender, cause, vehicle_involved_id,
--   patient_victim_id, accident_type_id, severity
-- )
-- FROM '/tmp/file_name.csv'
-- DELIMITER ',' CSV HEADER;