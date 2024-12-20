package org.irs.service;

import org.irs.dto.AccidentReportRequestDTO;
import org.irs.dto.AccidentReportResponseDTO;
import org.irs.util.ConstantValues;
import org.irs.util.GeneralMethods;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject; 

import org.irs.QueryStore.Report;
import org.irs.database.Datasources;
 
import java.sql.Connection; 
import java.sql.ResultSet;
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

    public AccidentReportResponseDTO saveAccidentReport(AccidentReportRequestDTO reportDTO) {
        AccidentReportResponseDTO responseDTO = new AccidentReportResponseDTO();
         // Process image data
         try {

            // Process image data
            if (reportDTO.imageData != null && !reportDTO.imageData.isEmpty()) {
                String imagePath = generalMethods.saveBase64ToFile(reportDTO.imageData, "image", ".jpg");
                reportDTO.imageUri = imagePath; // Set the path for saving in DB
            }

            // Process audio data
            if (reportDTO.audioData != null && !reportDTO.audioData.isEmpty()) {
                String audioPath = generalMethods.saveBase64ToFile(reportDTO.audioData, "audio", ".mp3");
                reportDTO.audioUri = audioPath; // Set the path for saving in DB
            }

            String query =queryStore.getInsertAccidentReportQuery(reportDTO);
    
        
            try (Connection con = datasource.getConnection(); Statement stmt = con.createStatement()) {
                // Execute the insert query and return generated keys
                int rowsAffected = stmt.executeUpdate(query, Statement.RETURN_GENERATED_KEYS);
                responseDTO.rowsInserted = String.valueOf(rowsAffected);

                // Get the generated ID (report_id)
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        long generatedId = rs.getLong(1);
                        responseDTO.id = String.valueOf(generatedId);
                    }
                }

                System.out.println("Inserted Successfully");

            }

        } catch (Exception ex) {
            ex.printStackTrace();
            responseDTO.error = "Error saving accident report: " + ex.getMessage();
        }

        return responseDTO;
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
