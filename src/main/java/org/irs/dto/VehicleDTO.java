package org.irs.dto;


public class VehicleDTO {
    public String registrationNo;
    public Integer type;
    public String condition;
    public String fitnessCertificateStatus;
    public String roadTaxStatus;
    public String insuranceStatus;
    public String getRegistrationNo() {
        return registrationNo;
    }
    public void setRegistrationNo(String registrationNo) {
        this.registrationNo = registrationNo;
    }
    public Integer getType() {
        return type;
    }
    public void setType(Integer type) {
        this.type = type;
    }
    public String getCondition() {
        return condition;
    }
    public void setCondition(String condition) {
        this.condition = condition;
    }
    public String getFitnessCertificateStatus() {
        return fitnessCertificateStatus;
    }
    public void setFitnessCertificateStatus(String fitnessCertificateStatus) {
        this.fitnessCertificateStatus = fitnessCertificateStatus;
    }
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