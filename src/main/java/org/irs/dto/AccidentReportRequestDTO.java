package org.irs.dto;

import java.util.List;

public class AccidentReportRequestDTO {
    public Double latitude;
    public Double longitude;
    public String location;
    public Integer vehicleInvolvedId;
    public Integer patientVictimId;
    public Integer accidentTypeId;
    public Integer userId;
    public Integer cause;
    public Integer numAffecties;
    public Integer age;
    public Integer gender;
    public Integer weatherCondition;
    public Integer visibility;
    public Integer roadSurfaceCondition;
    public Integer roadType;
    public Integer roadMarkings; 
    public Integer preliminaryFault;
    public String imageUri;
    public String audioUri;
    public String imageData;
    public String audioData;
    public String imagePath;
    public String audioPath;
    public String status;
    public String description;
    public String createdAt;
    public String nearestLandMark;
    public String videoUri;
    public String officerName;
    public String officerDesignation;
    public String officerContactNo;
   
    public String officerNotes;

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
        return "AccidentReportRequestDTO [latitude=" + latitude + ", longitude=" + longitude + ", location=" + location
                + ", vehicleInvolvedId=" + vehicleInvolvedId + ", patientVictimId=" + patientVictimId
                + ", accidentTypeId=" + accidentTypeId + ", userId=" + userId + ", cause=" + cause + ", numAffecties="
                + numAffecties + ", age=" + age + ", gender=" + gender + ", imageUri=" + imageUri + ", audioUri="
                + audioUri + ", imageData=" + imageData + ", audioData=" + audioData + ", imagePath=" + imagePath
                + ", audioPath=" + audioPath + ", status=" + status + ", description=" + description + ", createdAt="
                + createdAt + ", nearestLandMark=" + nearestLandMark + "]";
    }
     
}


