package org.irs.dto;

import java.util.List;
import java.util.Map;

public class AccidentReportResponseDTO {
   public String id;
   public String error;
   public String rowsInserted;
   public Double latitude;
   public Double longitude;
   public String location;
   public String vehicleInvolvedId;
   public String patientVictimId;
   public String accidentTypeId;
   public String userId;
   public String cause;
   public String numAffecties;
   public String age;
   public String gender;
   public String imageUri;
   public String audioUri;
   public String status;
   public String description;
   public String createdAt;
   public String vehicleLabel;
   public String vehicleDescription; 
   public String victimLabel;
   public String victimDescription; 
   public String accidentTypeLabel;
   public String accidentTypeDescription; 
   public String reportedBy; 
   public String imageData;
   public String audioData;
   public String severity;
   
   // Related entities for full report details
    public List<VehicleDTO> vehicles;
    public List<DriverDTO> drivers;
    public List<PassengerDTO> casualties;
    public List<WitnessDTO> witnesses;
    public FollowUpDTO followUp;
    public EvidenceDTO evidence;
    public List<ImageDTO> imageDTOs;
    public List<VehicleFitnessDTO> vehicleFitnessDetails;


    public String weatherCondition;
    public String visibility;
    public String roadSurfaceCondition;
    public String roadType;
    public String roadMarkings; 
    public String preliminaryFault;
    public String nearestLandMark;
    public String officerName;
    public String officerDesignation;
    public String officerContactNo;
    public String officerNotes;

    @Override
    public String toString() {
        return "AccidentReportResponseDTO [id=" + id + ", error=" + error + ", rowsInserted=" + rowsInserted
                + ", latitude=" + latitude + ", longitude=" + longitude + ", location=" + location
                + ", vehicleInvolvedId=" + vehicleInvolvedId + ", patientVictimId=" + patientVictimId
                + ", accidentTypeId=" + accidentTypeId + ", userId=" + userId + ", cause=" + cause + ", numAffecties="
                + numAffecties + ", age=" + age + ", gender=" + gender + ", imageUri=" + imageUri + ", audioUri="
                + audioUri + ", status=" + status + ", description=" + description + ", createdAt=" + createdAt
                + ", vehicleLabel=" + vehicleLabel + ", vehicleDescription=" + vehicleDescription + ", victimLabel="
                + victimLabel + ", victimDescription=" + victimDescription + ", accidentTypeLabel=" + accidentTypeLabel
                + ", accidentTypeDescription=" + accidentTypeDescription + ", reportedBy=" + reportedBy + ", imageData="
                + imageData + ", audioData=" + audioData + "]";
    }

}
