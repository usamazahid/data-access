package org.irs.dto;

public class AccidentReportRequestDTO {
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
    public String imageData;
    public String audioData;
    public String imagePath;
    public String audioPath;
    public String status;
    public String description;
    public String createdAt;
    public String nearestLandMark;
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
