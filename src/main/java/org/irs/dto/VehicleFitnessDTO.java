package org.irs.dto;

public class VehicleFitnessDTO {
    public String vehicleNo;
    public boolean fitnessCertificateValid;
    public String expiryDate;
    public Integer roadTaxStatus;
    public Integer insuranceStatus;
    public String fitness_id;
    

    // Getters and Setters
    public String getVehicleNo() { return vehicleNo; }
    public void setVehicleNo(String vehicleNo) { this.vehicleNo = vehicleNo; }

    public boolean isFitnessCertificateValid() { return fitnessCertificateValid; }
    public void setFitnessCertificateValid(boolean fitnessCertificateValid) { this.fitnessCertificateValid = fitnessCertificateValid; }

    public String getExpiryDate() { return expiryDate; }
    public void setExpiryDate(String expiryDate) { this.expiryDate = expiryDate; }
    public Integer getRoadTaxStatus() {
        return roadTaxStatus;
    }
    public void setRoadTaxStatus(Integer roadTaxStatus) {
        this.roadTaxStatus = roadTaxStatus;
    }
    public Integer getInsuranceStatus() {
        return insuranceStatus;
    }
    public void setInsuranceStatus(Integer insuranceStatus) {
        this.insuranceStatus = insuranceStatus;
    }
}
