
    
package org.irs.dto;

public class DispatchRequestDto {
    public Integer dispatchId;
    public Integer reportId;
    public Integer ambulanceId;
    public Integer driverId;
    public Integer assignedBy;
    public String pickupTime;
    public String dropTime;
    public Double latitude;
    public Double longitude;
    public String dropLocation;
    public Integer hospitalId;
    public String status;
    @Override
    public String toString() {
        return "DispatchRequestDto [dispatchId=" + dispatchId + ", reportId=" + reportId + ", ambulanceId="
                + ambulanceId + ", driverId=" + driverId + ", assignedBy=" + assignedBy + ", pickupTime=" + pickupTime
                + ", dropTime=" + dropTime + ", latitude=" + latitude + ", longitude=" + longitude + ", dropLocation="
                + dropLocation + ", hospitalId=" + hospitalId + ", status=" + status + "]";
    }
    public Integer getDispatchId() {
        return dispatchId;
    }
    public void setDispatchId(Integer dispatchId) {
        this.dispatchId = dispatchId;
    }
    public Integer getReportId() {
        return reportId;
    }
    public void setReportId(Integer reportId) {
        this.reportId = reportId;
    }
    public Integer getAmbulanceId() {
        return ambulanceId;
    }
    public void setAmbulanceId(Integer ambulanceId) {
        this.ambulanceId = ambulanceId;
    }
    public Integer getDriverId() {
        return driverId;
    }
    public void setDriverId(Integer driverId) {
        this.driverId = driverId;
    }
   
    public String getPickupTime() {
        return pickupTime;
    }
    public void setPickupTime(String pickupTime) {
        this.pickupTime = pickupTime;
    }
    public String getDropTime() {
        return dropTime;
    }
    public void setDropTime(String dropTime) {
        this.dropTime = dropTime;
    }
   
    public String getDropLocation() {
        return dropLocation;
    }
    public void setDropLocation(String dropLocation) {
        this.dropLocation = dropLocation;
    }
    public Integer getHospitalId() {
        return hospitalId;
    }
    public void setHospitalId(Integer hospitalId) {
        this.hospitalId = hospitalId;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public Integer getAssignedBy() {
        return assignedBy;
    }
    public void setAssignedBy(Integer assignedBy) {
        this.assignedBy = assignedBy;
    }
    public Double getLatitude() {
        return latitude;
    }
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }
    public Double getLongitude() {
        return longitude;
    }
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
    

}
