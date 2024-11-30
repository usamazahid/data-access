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

}
