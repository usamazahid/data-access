package org.irs.service;

import org.irs.dto.AccidentReportRequestDTO;
import org.irs.dto.AccidentReportResponseDTO;
import org.irs.dto.DriverDTO;
import org.irs.dto.ImageDTO;
import org.irs.dto.PassengerDTO;
import org.irs.dto.VehicleDTO;
import org.irs.dto.VehicleFitnessDTO;
import org.irs.dto.WitnessDTO;
import org.irs.util.ConstantValues;
import org.irs.util.GeneralMethods;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import org.irs.QueryStore.Report;
import org.irs.database.Datasources;
 
import java.sql.Connection; 
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement; 
import java.util.ArrayList;
import java.util.List;

@RequestScoped
public class AccidentReportService {

    @Inject
    Datasources datasource;

    @Inject
    Report queryStore;

    @Inject
    GeneralMethods generalMethods;

    @Transactional
    public AccidentReportResponseDTO saveAccidentReport(AccidentReportRequestDTO reportDTO) {
        AccidentReportResponseDTO responseDTO = new AccidentReportResponseDTO();
        int totalRowsInserted = 0;
    
        try (Connection con = datasource.getConnection(); Statement stmt = con.createStatement()) {
            // Handle image & audio files
            if (reportDTO.imageData != null && !reportDTO.imageData.isEmpty()) {
                String imagePath = generalMethods.saveBase64ToFile(reportDTO.imageData, "image", ".jpg");
                reportDTO.imageUri = imagePath;
            }
            if (reportDTO.audioData != null && !reportDTO.audioData.isEmpty()) {
                String audioPath = generalMethods.saveBase64ToFile(reportDTO.audioData, "audio", ".mp3");
                reportDTO.audioUri = audioPath;
            }
    
            // Save the main accident report
            String reportQuery = queryStore.getInsertAccidentReportQuery(reportDTO);
            int rowsAffected = stmt.executeUpdate(reportQuery, Statement.RETURN_GENERATED_KEYS);
            totalRowsInserted += rowsAffected;
    
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    long reportId = rs.getLong(1);
                    responseDTO.id = String.valueOf(reportId);
    
                    // Insert related records and count rows
                    totalRowsInserted += insertRelatedRecords(reportId, reportDTO, stmt);
                    totalRowsInserted += insertReportImages(reportId, reportDTO, stmt);
                }
            }
    
        } catch (Exception ex) {
            ex.printStackTrace();
            responseDTO.error = "Error saving accident report: " + ex.getMessage();
            throw new RuntimeException(ex); // Let Quarkus handle rollback
        }
    
        responseDTO.rowsInserted = String.valueOf(totalRowsInserted);
        return responseDTO;
    }
    
   
    private int insertRelatedRecords(Long reportId, AccidentReportRequestDTO reportDTO, Statement stmt) throws SQLException {
        int rowsInserted = 0;

        if (reportDTO.vehicles != null && !reportDTO.vehicles.isEmpty()) {
            for (VehicleDTO vehicle : reportDTO.vehicles) {
                rowsInserted += stmt.executeUpdate(queryStore.getInsertVehicleQuery(vehicle, reportId));
            }
        }
        if (reportDTO.drivers != null && !reportDTO.drivers.isEmpty()) {
            for (DriverDTO driver : reportDTO.drivers) {
                rowsInserted += stmt.executeUpdate(queryStore.getInsertDriverQuery(driver, reportId));
            }
        }
        if (reportDTO.casualties != null && !reportDTO.casualties.isEmpty()) {
            for (PassengerDTO passenger : reportDTO.casualties) {
                rowsInserted += stmt.executeUpdate(queryStore.getInsertPassengerQuery(passenger, reportId));
            }
        }
        if (reportDTO.witnesses != null && !reportDTO.witnesses.isEmpty()) {
            for (WitnessDTO witness : reportDTO.witnesses) {
                rowsInserted += stmt.executeUpdate(queryStore.getInsertWitnessQuery(witness, reportId));
            }
        }

        if (reportDTO.vehicleFitnessDetails != null && !reportDTO.vehicleFitnessDetails.isEmpty()) {
            for (VehicleFitnessDTO fitness : reportDTO.vehicleFitnessDetails) {
                String fitnessQuery = queryStore.getInsertVehicleFitnessQuery(fitness, reportId);
                rowsInserted += stmt.executeUpdate(fitnessQuery);
            }
        }
        if (reportDTO.followUp != null) {
            rowsInserted += stmt.executeUpdate(queryStore.getInsertFollowUpQuery(reportDTO.followUp, reportId));
        }
        if (reportDTO.evidence != null) {
            rowsInserted += stmt.executeUpdate(queryStore.getInsertEvidenceQuery(reportDTO.evidence, reportId));
        }

        return rowsInserted;
    }


    private int insertReportImages(Long reportId, AccidentReportRequestDTO reportDTO, Statement stmt) throws Exception {
        int rowsInserted = 0;

        if (reportDTO.imageDTOs != null && !reportDTO.imageDTOs.isEmpty()) {
            for (ImageDTO imageDTO : reportDTO.imageDTOs) {
                if (imageDTO.imageData != null && !imageDTO.imageData.isEmpty()) {
                    String imagePath = generalMethods.saveBase64ToFile(imageDTO.imageData, "image", ".jpg");
                    String imageQuery = queryStore.getInsertImageQuery(imagePath, reportId);
                    rowsInserted += stmt.executeUpdate(imageQuery);
                }
            }
        }

        return rowsInserted;
    }
    

    public AccidentReportResponseDTO getAccidentReportById(String reportId) {
        String query = queryStore.getSelectByReportId(reportId);
        AccidentReportResponseDTO responseDTO = new AccidentReportResponseDTO();
    
        try (Connection con = datasource.getConnection(); 
             Statement stmt = con.createStatement(); 
             ResultSet rs = stmt.executeQuery(query)) {
    
            if (rs.next()) {
                responseDTO.id = String.valueOf(rs.getLong("report_id"));
                responseDTO.latitude = rs.getDouble("latitude");
                responseDTO.longitude = rs.getDouble("longitude");
                responseDTO.location = rs.getString("location");
                responseDTO.vehicleInvolvedId = rs.getInt("vehicle_involved_id");
                responseDTO.patientVictimId = rs.getInt("patient_victim_id");
                responseDTO.accidentTypeId = rs.getInt("accident_type_id");
                responseDTO.userId = rs.getInt("user_id");
                responseDTO.cause = rs.getString("cause");
                responseDTO.numAffecties = rs.getInt("num_affecties");
                responseDTO.age = rs.getInt("age");
                responseDTO.gender = rs.getString("gender");
                responseDTO.imageUri = rs.getString("image_uri");
                responseDTO.audioUri = rs.getString("audio_uri");
                responseDTO.status = rs.getString("status");
                responseDTO.description = rs.getString("description");
                responseDTO.createdAt = rs.getString("created_at");  
           
            } else {
                responseDTO.error = "No report found with the provided ID.";
                responseDTO.rowsInserted = "0";
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            responseDTO.error = "Error fetching accident report: " + ex.getMessage();
            responseDTO.rowsInserted = "0";
        }
    
        return responseDTO;
    }

    public List<AccidentReportResponseDTO> getAccidentReportsByUserId(String userId) {
        String query = queryStore.getSelectByUserId(userId);
        List<AccidentReportResponseDTO> reports = new ArrayList<>();
    
        try (Connection con = datasource.getConnection(); 
             Statement stmt = con.createStatement(); 
             ResultSet rs = stmt.executeQuery(query)) {
    
            while (rs.next()) {
                AccidentReportResponseDTO responseDTO = new AccidentReportResponseDTO();
                responseDTO.id = String.valueOf(rs.getLong("report_id")); 
                responseDTO.latitude = rs.getDouble("latitude");
                responseDTO.longitude = rs.getDouble("longitude");
                responseDTO.location = rs.getString("location");
                responseDTO.vehicleInvolvedId = rs.getInt("vehicle_involved_id");
                responseDTO.patientVictimId = rs.getInt("patient_victim_id");
                responseDTO.accidentTypeId = rs.getInt("accident_type_id");
                responseDTO.userId = rs.getInt("user_id");
                responseDTO.cause = rs.getString("cause");
                responseDTO.numAffecties = rs.getInt("num_affecties");
                responseDTO.age = rs.getInt("age");
                responseDTO.gender = rs.getString("gender");
                responseDTO.imageUri = rs.getString("image_uri");
                responseDTO.audioUri = rs.getString("audio_uri");
                responseDTO.status = rs.getString("status");
                responseDTO.description = rs.getString("description");
                responseDTO.createdAt = rs.getString("created_at"); 
            
                reports.add(responseDTO);
            }
    
            // If no reports found
            if (reports.isEmpty()) {
                AccidentReportResponseDTO responseDTO = new AccidentReportResponseDTO();
                responseDTO.error = "No reports found for the provided user ID.";
                responseDTO.rowsInserted = "0";
                reports.add(responseDTO);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            // Handle exception and add an error response
            AccidentReportResponseDTO responseDTO = new AccidentReportResponseDTO();
            responseDTO.error = "Error fetching reports: " + ex.getMessage();
            responseDTO.rowsInserted = "0";
            reports.add(responseDTO);
        }
    
        return reports;
    }

    public AccidentReportResponseDTO getJoinedAccidentReportById(String reportId) {
        String query = queryStore.getJoinedAccidentReportsById(reportId);
        AccidentReportResponseDTO responseDTO = new AccidentReportResponseDTO();
    
        try (Connection con = datasource.getConnection(); 
             Statement stmt = con.createStatement(); 
             ResultSet rs = stmt.executeQuery(query)) {
    
            if (rs.next()) {
                responseDTO.id = String.valueOf(rs.getLong("report_id"));
                responseDTO.latitude = rs.getDouble("latitude");
                responseDTO.longitude = rs.getDouble("longitude");
                responseDTO.location = rs.getString("location");
                responseDTO.vehicleInvolvedId = rs.getInt("vehicle_involved_id");
                responseDTO.vehicleLabel = rs.getString("vehicle_label");
                responseDTO.vehicleDescription = rs.getString("vehicle_description");
                responseDTO.patientVictimId = rs.getInt("patient_victim_id");
                responseDTO.victimLabel = rs.getString("victim_label");
                responseDTO.victimDescription = rs.getString("victim_description");
                responseDTO.accidentTypeId = rs.getInt("accident_type_id");
                responseDTO.accidentTypeLabel = rs.getString("accident_type_label");
                responseDTO.accidentTypeDescription = rs.getString("accident_type_description");
                responseDTO.userId = rs.getInt("user_id");
                responseDTO.reportedBy = rs.getString("reported_by");
                responseDTO.cause = rs.getString("cause");
                responseDTO.numAffecties = rs.getInt("num_affecties");
                responseDTO.age = rs.getInt("age");
                responseDTO.gender = rs.getString("gender");
                responseDTO.imageUri = rs.getString("image_uri");
                responseDTO.audioUri = rs.getString("audio_uri");
                responseDTO.status = rs.getString("status");
                responseDTO.description = rs.getString("description");
                responseDTO.createdAt = rs.getString("created_at");
           
            } else {
                responseDTO.error = "No report found with the provided ID.";
                responseDTO.rowsInserted = "0";
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            responseDTO.error = "Error fetching accident report: " + ex.getMessage();
            responseDTO.rowsInserted = "0";
        }
        System.out.println(responseDTO);
        return responseDTO;
    }

    public List<AccidentReportResponseDTO> getJoinedAccidentReportsByUserId(String userId) {
        String query = queryStore.getJoinedAccidentReportsByUserId(userId);
        List<AccidentReportResponseDTO> reports = new ArrayList<>();
    
        try (Connection con = datasource.getConnection(); 
             Statement stmt = con.createStatement(); 
             ResultSet rs = stmt.executeQuery(query)) {
    
            while (rs.next()) {
                AccidentReportResponseDTO responseDTO = new AccidentReportResponseDTO();
                responseDTO.id = String.valueOf(rs.getLong("report_id"));
                responseDTO.latitude = rs.getDouble("latitude");
                responseDTO.longitude = rs.getDouble("longitude");
                responseDTO.location = rs.getString("location");
                responseDTO.vehicleInvolvedId = rs.getInt("vehicle_involved_id");
                responseDTO.vehicleLabel = rs.getString("vehicle_label");
                responseDTO.vehicleDescription = rs.getString("vehicle_description");
                responseDTO.patientVictimId = rs.getInt("patient_victim_id");
                responseDTO.victimLabel = rs.getString("victim_label");
                responseDTO.victimDescription = rs.getString("victim_description");
                responseDTO.accidentTypeId = rs.getInt("accident_type_id");
                responseDTO.accidentTypeLabel = rs.getString("accident_type_label");
                responseDTO.accidentTypeDescription = rs.getString("accident_type_description");
                responseDTO.userId = rs.getInt("user_id");
                responseDTO.reportedBy = rs.getString("reported_by");
                responseDTO.cause = rs.getString("cause");
                responseDTO.numAffecties = rs.getInt("num_affecties");
                responseDTO.age = rs.getInt("age");
                responseDTO.gender = rs.getString("gender");
                responseDTO.imageUri = rs.getString("image_uri");
                responseDTO.audioUri = rs.getString("audio_uri");
                responseDTO.status = rs.getString("status");
                responseDTO.description = rs.getString("description");
                responseDTO.createdAt = rs.getString("created_at");
            
                reports.add(responseDTO);
            }
    
            // If no reports found
            if (reports.isEmpty()) {
                AccidentReportResponseDTO responseDTO = new AccidentReportResponseDTO();
                responseDTO.error = "No reports found for the provided user ID.";
                responseDTO.rowsInserted = "0";
                reports.add(responseDTO);
            }

            System.out.println(reports);
        } catch (Exception ex) {
            ex.printStackTrace();
            // Handle exception and add an error response
            AccidentReportResponseDTO responseDTO = new AccidentReportResponseDTO();
            responseDTO.error = "Error fetching reports: " + ex.getMessage();
            responseDTO.rowsInserted = "0";
            reports.add(responseDTO);
        }
    
        return reports;
    }
    public String getFileBase64Response(String fileName){
        try {
            String filePath=ConstantValues.BASE_DIR+fileName;
            return  generalMethods.readFileAsBase64(filePath);
        } catch (Exception e) {
            Log.info(e);
            return null;
        }
    }

    public AccidentReportResponseDTO getReportFiles(String reportId) {
        String query = queryStore.getSelectByReportId(reportId);
        AccidentReportResponseDTO responseDTO = new AccidentReportResponseDTO();
    
        try (Connection con = datasource.getConnection(); 
             Statement stmt = con.createStatement(); 
             ResultSet rs = stmt.executeQuery(query)) {
    
            if (rs.next()) {
                responseDTO.id = String.valueOf(rs.getLong("report_id"));

                responseDTO.imageUri = rs.getString("image_uri");
                responseDTO.audioUri = rs.getString("audio_uri"); 
                    // Convert files to Base64 if they exist
                if (responseDTO.imageUri != null && !responseDTO.imageUri.isEmpty()) {
                    responseDTO.imageData = generalMethods.readFileAsBase64(responseDTO.imageUri);
                }
                if (responseDTO.audioUri != null && !responseDTO.audioUri.isEmpty()) {
                    responseDTO.audioData = generalMethods.readFileAsBase64(responseDTO.audioUri);
                }
           
            } else {
                responseDTO.error = "No report found with the provided ID.";
                responseDTO.rowsInserted = "0";
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            responseDTO.error = "Error fetching accident report: " + ex.getMessage();
            responseDTO.rowsInserted = "0";
        }
    
        return responseDTO;
    }
    

}
