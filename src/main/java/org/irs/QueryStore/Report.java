package org.irs.QueryStore;

import java.sql.Timestamp;

import org.irs.dto.AccidentReportRequestDTO;

import jakarta.inject.Singleton;

@Singleton
public class Report {

    public String getInsertAccidentReportQuery(AccidentReportRequestDTO reportDTO) {
        return "INSERT INTO public.accident_reports (latitude, longitude, location, vehicle_involved_id, " +
                       "patient_victim_id, accident_type_id, user_id, cause, num_affecties, age, gender, " +
                       "image_uri, audio_uri, status, description, created_at) VALUES ( " +
                       reportDTO.latitude + ", " +
                       reportDTO.longitude + ", '" +
                       reportDTO.location + "', " +
                       (reportDTO.vehicleInvolvedId != null ? reportDTO.vehicleInvolvedId : "NULL") + ", " +
                       (reportDTO.patientVictimId != null ? reportDTO.patientVictimId : "NULL") + ", " +
                       (reportDTO.accidentTypeId != null ? reportDTO.accidentTypeId : "NULL") + ", " +
                       (reportDTO.userId != null ? reportDTO.userId : "NULL") + ", '" +
                       reportDTO.cause + "', " +
                       (reportDTO.numAffecties != null ? reportDTO.numAffecties : "NULL") + ", " +
                       (reportDTO.age != null ? reportDTO.age : "NULL") + ", '" +
                       reportDTO.gender + "', '" +
                       (reportDTO.imageUri != null ? reportDTO.imageUri : "") + "', '" +
                       (reportDTO.audioUri != null ? reportDTO.audioUri : "") + "', '" +
                       (reportDTO.status != null ? reportDTO.status : "pending") + "', '" +
                       (reportDTO.description != null ? reportDTO.description : "") + "', '" +
                       (reportDTO.createdAt != null ? reportDTO.createdAt : new Timestamp(System.currentTimeMillis())) + "')";
    }

    public String getSelectByReportId(String reportId) {
        return "SELECT * FROM public.accident_reports WHERE report_id = " + reportId;
    }

    public String getSelectByUserId(String userId) {
        return "SELECT * FROM public.accident_reports WHERE user_id = " + userId;
    }

    public String getJoinedAccidentReportsById(String reportId) {
        return """
            SELECT 
                ar.report_id, 
                ar.latitude, 
                ar.longitude, 
                ar.location, 
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
                ar.location, 
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
            WHERE u.id = 
        """+userId;
    }

    
}
