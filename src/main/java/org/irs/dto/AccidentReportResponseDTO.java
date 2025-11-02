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
   public Integer vehicleInvolvedId;
   public Integer patientVictimId;
   public Integer accidentTypeId;
   public Integer userId;
   public String cause;
   public Integer numAffecties;
   public Integer age;
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
   public Integer severity;
   
   // Related entities for full report details
    public List<VehicleDTO> vehicles;
    public List<DriverDTO> drivers;
    public List<PassengerDTO> casualties;
    public List<WitnessDTO> witnesses;
    public FollowUpDTO followUp;
    public EvidenceDTO evidence;
    public List<ImageDTO> imageDTOs;
    public List<VehicleFitnessDTO> vehicleFitnessDetails;
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
