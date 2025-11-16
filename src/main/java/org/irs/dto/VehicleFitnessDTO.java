package org.irs.dto;

public class VehicleFitnessDTO {
    public String vehicleNo;
    public boolean fitnessCertificateValid;
    public String expiryDate;
    public String roadTaxStatus;
    public String insuranceStatus;
    public String fitness_id;
    

    // Getters and Setters
    public String getVehicleNo() { return vehicleNo; }
    public void setVehicleNo(String vehicleNo) { this.vehicleNo = vehicleNo; }

    public boolean isFitnessCertificateValid() { return fitnessCertificateValid; }
    public void setFitnessCertificateValid(boolean fitnessCertificateValid) { this.fitnessCertificateValid = fitnessCertificateValid; }

    public String getExpiryDate() { return expiryDate; }
    public void setExpiryDate(String expiryDate) { this.expiryDate = expiryDate; }
    public String getRoadTaxStatus() {
        return roadTaxStatus;
    }
    public void setRoadTaxStatus(String roadTaxStatus) {
        this.roadTaxStatus = roadTaxStatus;
    }
    public String getInsuranceStatus() {
        return insuranceStatus;
    }
    public void setInsuranceStatus(String insuranceStatus) {
        this.insuranceStatus = insuranceStatus;
    }
}
