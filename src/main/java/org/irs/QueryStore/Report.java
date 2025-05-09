package org.irs.QueryStore;


import jakarta.inject.Singleton;
import java.util.function.Function;
import org.irs.dto.AccidentReportRequestDTO;
import org.irs.dto.DriverDTO;
import org.irs.dto.EvidenceDTO;
import org.irs.dto.FollowUpDTO;
import org.irs.dto.PassengerDTO;
import org.irs.dto.VehicleDTO;
import org.irs.dto.VehicleFitnessDTO;
import org.irs.dto.WitnessDTO;



@Singleton
public class Report {
    public String getInsertAccidentReportQuery(AccidentReportRequestDTO reportDTO) {
    // Helper for escaping strings safely
    Function<String, String> escapeStr = str -> str != null ? "'" + str.replace("'", "''") + "'" : "NULL";

    String query = "INSERT INTO public.accident_reports (" +
            "gis_coordinates, latitude, longitude, accident_location, vehicle_involved_id, " +
            "patient_victim_id, accident_type_id, user_id, cause, num_affecties, age, gender, " +
            "image_uri, audio_uri, video_uri, status, description, weather_condition, visibility, " +
            "road_surface_condition, road_type, road_markings,preliminary_fault, officer_name, officer_designation, officer_contact_no, " +
            "officer_notes) VALUES (" +

            // PostGIS point
            "ST_GeomFromText('POINT(" + reportDTO.longitude + " " + reportDTO.latitude + ")', 4326), " +

            // Latitude & Longitude
            (reportDTO.latitude != null ? reportDTO.latitude : "0") + ", " +
            (reportDTO.longitude != null ? reportDTO.longitude : "0") + ", " +

            // Strings
            escapeStr.apply(reportDTO.nearestLandMark) + ", " +

            // Integers (IDs, int values)
            (reportDTO.vehicleInvolvedId != null ? reportDTO.vehicleInvolvedId : "NULL") + ", " +
            (reportDTO.patientVictimId != null ? reportDTO.patientVictimId : "NULL") + ", " +
            (reportDTO.accidentTypeId != null ? reportDTO.accidentTypeId : "NULL") + ", " +
            (reportDTO.userId != null ? reportDTO.userId : "NULL") + ", " +
            (reportDTO.cause != null ? reportDTO.cause : "NULL") + ", " + // cause is int
            (reportDTO.numAffecties != null ? reportDTO.numAffecties : "NULL") + ", " +
            (reportDTO.age != null ? reportDTO.age : "NULL") + ", " +
            (reportDTO.gender != null ? reportDTO.gender : "NULL") + ", " +
            // Strings
            escapeStr.apply(reportDTO.imageUri != null ? reportDTO.imageUri : "") + ", " +
            escapeStr.apply(reportDTO.audioUri != null ? reportDTO.audioUri : "") + ", " +
            escapeStr.apply(reportDTO.videoUri != null ? reportDTO.videoUri : "") + ", " +
            escapeStr.apply(reportDTO.status != null ? reportDTO.status : "pending") + ", " +
            escapeStr.apply(reportDTO.description != null ? reportDTO.description : "") + ", " +

            // Integers
            (reportDTO.weatherCondition != null ? reportDTO.weatherCondition : "NULL") + ", " +
            (reportDTO.visibility != null ? reportDTO.visibility : "NULL") + ", " +
            (reportDTO.roadSurfaceCondition != null ? reportDTO.roadSurfaceCondition : "NULL") + ", " +
            (reportDTO.roadType != null ? reportDTO.roadType : "NULL") + ", " +
            (reportDTO.roadMarkings != null ? reportDTO.roadMarkings : "NULL") + ", " +
            (reportDTO.preliminaryFault != null ? reportDTO.preliminaryFault : "NULL") + ", " +
            // Strings
            escapeStr.apply(reportDTO.officerName != null ? reportDTO.officerName : "") + ", " +
            escapeStr.apply(reportDTO.officerDesignation != null ? reportDTO.officerDesignation : "") + ", " +
            escapeStr.apply(reportDTO.officerContactNo != null ? reportDTO.officerContactNo : "") + ", " +
            escapeStr.apply(reportDTO.officerNotes != null ? reportDTO.officerNotes : "") +

            ")";
    
    System.out.println(query);
    return query;
}

    public String getSelectByReportId(String reportId) {
        return """
            Select 
                report_id, 
                latitude, 
                longitude, 
                accident_location as location, 
                vehicle_involved_id, 
                patient_victim_id, 
                accident_type_id, 
                user_id, 
                cause, 
                num_affecties, 
                age, 
                gender, 
                image_uri, 
                audio_uri, 
                status, 
                description, 
                created_at 
            From 
                public.accident_reports  WHERE report_id = """+ reportId;
    }

    public String getSelectByUserId(String userId) {
        return """
            Select 
                report_id, 
                latitude, 
                longitude, 
                accident_location as location, 
                vehicle_involved_id, 
                patient_victim_id, 
                accident_type_id, 
                user_id, 
                cause, 
                num_affecties, 
                age, 
                gender, 
                image_uri, 
                audio_uri, 
                status, 
                description, 
                created_at 
            From 
                public.accident_reports WHERE user_id = """ + userId;
    }

    public String getJoinedAccidentReportsById(String reportId) {
        return """
            SELECT 
                ar.report_id, 
                ar.latitude, 
                ar.longitude, 
                ar.accident_location as location, 
                ar.vehicle_involved_id, 
                vi.label AS vehicle_label, 
                vi.description AS vehicle_description,
                ar.patient_victim_id, 
                pv.label AS victim_label, 
                pv.description AS victim_description,
                ar.accident_type_id, 
                at.label AS accident_type_label, 
                at.description AS accident_type_description,
                ar.user_id, 
                u.username AS reported_by,
                ar.cause, 
                ar.num_affecties, 
                ar.age, 
                ar.gender, 
                ar.image_uri, 
                ar.audio_uri, 
                ar.status, 
                ar.description, 
                ar.created_at
            FROM 
                public.accident_reports ar 
            LEFT JOIN public.vehicle_involved vi ON ar.vehicle_involved_id = vi.id
            LEFT JOIN public.patient_victim pv ON ar.patient_victim_id = pv.id
            LEFT JOIN public.accident_types at ON ar.accident_type_id = at.id
            LEFT JOIN public.users u ON ar.user_id = u.id
            WHERE ar.report_id = 
        """+reportId;
    }


    public String getJoinedAccidentReportsByUserId(String userId) {
        return """
            SELECT 
                ar.report_id, 
                ar.latitude, 
                ar.longitude, 
                ar.accident_location as location, 
                ar.vehicle_involved_id, 
                vi.label AS "vehicle_label", 
                vi.description AS "vehicle_description",
                ar.patient_victim_id, 
                pv.label AS victim_label, 
                pv.description AS "victim_description",
                ar.accident_type_id, 
                at.label AS "accident_type_label", 
                at.description AS "accident_type_description",
                ar.user_id, 
                u.username AS "reported_by",
                ar.cause, 
                ar.num_affecties, 
                ar.age, 
                ar.gender, 
                ar.image_uri, 
                ar.audio_uri, 
                ar.status, 
                ar.description, 
                ar.created_at
            FROM 
                public.accident_reports ar
            LEFT JOIN public.vehicle_involved vi ON ar.vehicle_involved_id = vi.id
            LEFT JOIN public.patient_victim pv ON ar.patient_victim_id = pv.id
            LEFT JOIN public.accident_types at ON ar.accident_type_id = at.id
            LEFT JOIN public.users u ON ar.user_id = u.id
            WHERE 
            u.id = """+userId+" ORDER BY ar.created_at DESC ";
    }

    public String getJoinedAccidentReportsByUserIdWithPagination(String userId, int offset, int limit) {
        return """
            SELECT 
                ar.report_id, 
                ar.latitude, 
                ar.longitude, 
                ar.accident_location as location, 
                ar.vehicle_involved_id, 
                vi.label AS "vehicle_label", 
                vi.description AS "vehicle_description",
                ar.patient_victim_id, 
                pv.label AS victim_label, 
                pv.description AS "victim_description",
                ar.accident_type_id, 
                at.label AS "accident_type_label", 
                at.description AS "accident_type_description",
                ar.user_id, 
                u.username AS "reported_by",
                ar.cause, 
                ar.num_affecties, 
                ar.age, 
                ar.gender, 
                ar.image_uri, 
                ar.audio_uri, 
                ar.status, 
                ar.description, 
                ar.created_at
            FROM 
                public.accident_reports ar
            LEFT JOIN public.vehicle_involved vi ON ar.vehicle_involved_id = vi.id
            LEFT JOIN public.patient_victim pv ON ar.patient_victim_id = pv.id
            LEFT JOIN public.accident_types at ON ar.accident_type_id = at.id
            LEFT JOIN public.users u ON ar.user_id = u.id
            WHERE u.id = """ + userId + 
            " ORDER BY ar.created_at DESC LIMIT "
             + (" "+limit + " OFFSET " + offset);
    }

        // 🚀 Query Methods (to be placed in QueryStore)

    public String getInsertVehicleQuery(VehicleDTO vehicle, Long reportId) {
        String query= "INSERT INTO vehicle_details (report_id, registration_no, type, condition, fitness_certificate_status, road_tax_status, insurance_status) VALUES ("
                + reportId + ", '" + vehicle.getRegistrationNo() + "', " + (vehicle.getType()!=null?vehicle.getType():"NULL") + ", '" + vehicle.getCondition() + "', '" + vehicle.getFitnessCertificateStatus() + "', '" + vehicle.getRoadTaxStatus() + "', '" + vehicle.getInsuranceStatus() + "');";
        System.out.println(query);
        return query;
        }

    public String getInsertDriverQuery(DriverDTO driver, Long reportId) {
        String query= "INSERT INTO driver_details (report_id, name, cnic_no, license_no, contact_no) VALUES ("
                + reportId + ", '" + driver.getName() + "', '" + driver.getCnicNo() + "', '" + driver.getLicenseNo() + "', '" + driver.getContactNo() + "');";
    
        System.out.println(query);
        return query;
        }

    public String getInsertPassengerQuery(PassengerDTO passenger, Long reportId) {
        String query= "INSERT INTO passenger_casualties (report_id, type, name, hospital_name, injury_severity) VALUES ("
                + reportId + ", '" + passenger.getType() + "', '" + passenger.getName() + "', '" + passenger.getHospitalName() + "', '" + passenger.getInjurySeverity() + "');";
    
        System.out.println(query);
        return query;    
        }

    public String getInsertWitnessQuery(WitnessDTO witness, Long reportId) {
        String query= "INSERT INTO witness_details (report_id, name, contact_no, address) VALUES ("
                + reportId + ", '" + witness.getName() + "', '" + witness.getContactNo() + "', '" + witness.getAddress() + "');";
            System.out.println(query);
            return query;
            }

    public String getInsertFollowUpQuery(FollowUpDTO followUp, Long reportId) {
        String query= "INSERT INTO follow_up_actions (report_id, fir_registered, fir_number, challan_issued, challan_number, case_referred_to) VALUES ("
                + reportId + ", " + followUp.isFirRegistered() + ", '" + followUp.getFirNumber() + "', " + followUp.isChallanIssued() + ", '" + followUp.getChallanNumber() + "', " + (followUp.getCaseReferredTo()!=null?followUp.getCaseReferredTo():"NULL" )+ ");";
            System.out.println(query);
            return query;
        }

    public String getInsertEvidenceQuery(EvidenceDTO evidence, Long reportId) {
        String query= "INSERT INTO evidence_collection (report_id, photos, videos, sketch) VALUES ("
                + reportId + ", " + evidence.isPhotosTaken() + ", " + evidence.isVideosRecorded() + ", " + evidence
                    .isSketchPrepared() + ");";
        System.out.println(query);
        return query;
        }

    public String getInsertImageQuery(String imageUri, Long reportId) {
        String query= "INSERT INTO public.accident_report_images (report_id, image_uri) VALUES (" + reportId + ", '" + imageUri + "');";
        System.out.println(query);
        return query;
    }

    public String getInsertVehicleFitnessQuery(VehicleFitnessDTO fitness, Long reportId) {
        String query= "INSERT INTO public.accident_vehicle_fitness (report_id, vehicle_no, fitness_certificate_valid, expiry_date, road_tax_status, insurance_status) VALUES (" +
                reportId + ", '" + fitness.getVehicleNo() + "', " + fitness.isFitnessCertificateValid() + ", '" + fitness.getExpiryDate() + "', '" + fitness.getRoadTaxStatus() + "', '" + fitness.getInsuranceStatus() + "');";
        System.out.println(query);
        return query;
        }

    public String getHeatMapData(String interval){
        String query= "SELECT report_id, ST_X(gis_coordinates) AS longitude, ST_Y(gis_coordinates) AS latitude, severity " +
        "FROM accident_reports " +
        "WHERE created_at >= NOW() - INTERVAL '" + interval + "' " +
        "ORDER BY created_at DESC " ;
        System.out.println(query);
        return query;
    }

    public String getHeatMapDataRangeLimit(String interval,Integer limit){
        if(limit==null || limit<1){
            limit=100; // Default limit
        }
        String query= "SELECT report_id, ST_X(gis_coordinates) AS longitude, ST_Y(gis_coordinates) AS latitude, severity " +
        "FROM accident_reports " +
        "WHERE created_at >= NOW() - INTERVAL '" + interval + "' " +
        "ORDER BY created_at DESC " +
        "LIMIT " + limit; // Limit the number of records
        System.out.println(query);
        return query;
    }


    public String getHeatMapDataWithLimit(Integer limit){
        if(limit==null || limit<1){
            limit=100; // Default limit
        }
        String query= "SELECT report_id, ST_X(gis_coordinates) AS longitude, ST_Y(gis_coordinates) AS latitude, severity " +
        "FROM accident_reports " +
        "ORDER BY created_at DESC " + // Order by latest records first
        "LIMIT " + limit; // Limit the number of records
        System.out.println(query);
        return query;
    }


    public String getFilteredHeatMapDataWithLimit(Integer limit, String vehicleType, String accidentType) {
        StringBuilder query = new StringBuilder();
        query.append("SELECT report_id, ST_X(gis_coordinates) AS longitude, ST_Y(gis_coordinates) AS latitude, severity ")
             .append("FROM accident_reports ar ");
        
        // Add joins if filters are present
        if (vehicleType != null) {
            query.append("LEFT JOIN vehicle_involved vi ON ar.vehicle_involved_id = vi.id ");
        }
        if (accidentType != null) {
            query.append("LEFT JOIN accident_types at ON ar.accident_type_id = at.id ");
        }
        
        query.append("WHERE 1=1 ");
        
        // Add filter conditions
        if (vehicleType != null) {
            query.append("AND vi.id = '").append(vehicleType).append("' ");
        }
        if (accidentType != null) {
            query.append("AND at.id = '").append(accidentType).append("' ");
        }
        
        query.append("ORDER BY ar.created_at DESC ");
        
        // Add limit
        if (limit == null || limit < 1) {
            limit = 100;
        }
        query.append("LIMIT ").append(limit);
        
        System.out.println("Filtered Query: " + query.toString());
        return query.toString();
    }

    public String getFilteredHeatMapData(String interval, Integer limit, String vehicleType, String accidentType) {
        StringBuilder query = new StringBuilder();
        query.append("SELECT report_id, ST_X(gis_coordinates) AS longitude, ST_Y(gis_coordinates) AS latitude, severity ")
             .append("FROM accident_reports ar ");
        
        // Add joins if filters are present
        
        if (vehicleType != null) {
            query.append("LEFT JOIN vehicle_involved vi ON ar.vehicle_involved_id = vi.id ");
        }
        if (accidentType != null) {
            query.append("LEFT JOIN accident_types at ON ar.accident_type_id = at.id ");
        }
    
        
        query.append("WHERE 1=1 ");
        
        // Add time range if specified
        if (interval != null) {
            query.append("AND ar.created_at >= NOW() - INTERVAL '").append(interval).append("' ");
        }
        
        // Add filter conditions
        if (vehicleType != null) {
            query.append("AND vi.id = '").append(vehicleType).append("' ");
        }
        if (accidentType != null) {
            query.append("AND at.id = '").append(accidentType).append("' ");
        }
        
        query.append("ORDER BY ar.created_at DESC ");
        
        // Add limit
        if (limit == null || limit < 1) {
            limit = 100;
        }
        query.append("LIMIT ").append(limit);
        
        System.out.println("Filtered Clustering Query: " + query.toString());
        return query.toString();
    }


    public String getFilteredHeatMapDataWithLimit(
        String interval,
        Integer limit,
        String vehicleType,
        String accidentType,
        String startDate,      // new
        String endDate,        // new
        String minSeverity,   // new
        Double swLat,          // new
        Double swLng,          // new
        Double neLat,          // new
        Double neLng           // new
    ) {
        StringBuilder q = new StringBuilder();
        q.append("SELECT report_id, ST_X(gis_coordinates) AS longitude, ")
        .append("ST_Y(gis_coordinates) AS latitude, severity ")
        .append("FROM accident_reports ar ");

        if (vehicleType != null) {
            q.append("LEFT JOIN vehicle_involved vi ON ar.vehicle_involved_id = vi.id ");
        }
        if (accidentType != null) {
            q.append("LEFT JOIN accident_types at ON ar.accident_type_id = at.id ");
        }

        q.append("WHERE 1=1 ");

        if (interval != null) {
            q.append("AND ar.created_at >= NOW() - INTERVAL '").append(interval).append("' ");
        }

        if (vehicleType != null) {
            q.append("AND vi.id = '").append(vehicleType).append("' ");
        }
        if (accidentType != null) {
            q.append("AND at.id = '").append(accidentType).append("' ");
        }
        if (startDate != null) {
            q.append("AND ar.created_at >= '").append(startDate).append("' ");
        }
        if (endDate != null) {
            q.append("AND ar.created_at <= '").append(endDate).append("' ");
        }
        if (minSeverity != null) {
            if(minSeverity.matches("[0-9]+")) {
                q.append("AND ar.severity >= ").append(minSeverity).append(" ");
            }
            else if(minSeverity.matches("[a-zA-Z]+")) {
                switch (minSeverity) {
                    case "low":
                        q.append("AND ar.severity BETWEEN 1 AND 3 ");
                        break;
                    case "medium":
                        q.append("AND ar.severity BETWEEN 4 AND 6 ");
                        break;
                    case "high":
                        q.append("AND ar.severity BETWEEN 7 AND 10 ");
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid severity level: " + minSeverity);
                }
            }
        }
        if (swLat!=null && swLng!=null && neLat!=null && neLng!=null) {
            q.append("AND ST_Y(gis_coordinates) BETWEEN ")
            .append(swLat).append(" AND ").append(neLat).append(" ")
            .append("AND ST_X(gis_coordinates) BETWEEN ")
            .append(swLng).append(" AND ").append(neLng).append(" ");
        }

        if (limit == null || limit < 1) limit = 100;
        q.append("ORDER BY ar.created_at DESC ")
        .append("LIMIT ").append(limit);

        System.out.println("Filtered Query: " + q);
        return q.toString();
    }

}
