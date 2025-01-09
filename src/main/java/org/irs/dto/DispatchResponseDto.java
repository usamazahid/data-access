
package org.irs.dto;

public class DispatchResponseDto {
    public String dispatchId;
    public String reportId;
    public String ambulanceId;
    public String driverId;
    public String assignedBy;
    public String pickupTime;
    public String dropTime;
    public String latitude;
    public String longitude;
    public String dropLocation;
    public String hospitalId;
    public String status;
    @Override
    public String toString() {
        return "DispatchRequestDto [dispatchId=" + dispatchId + ", reportId=" + reportId + ", ambulanceId="
                + ambulanceId + ", driverId=" + driverId + ", assignedBy=" + assignedBy + ", pickupTime=" + pickupTime
                + ", dropTime=" + dropTime + ", latitude=" + latitude + ", longitude=" + longitude + ", dropLocation="
                + dropLocation + ", hospitalId=" + hospitalId + ", status=" + status + "]";
    }
    public String getDispatchId() {
        return dispatchId;
    }
    public void setDispatchId(String dispatchId) {
        this.dispatchId = dispatchId;
    }
    public String getReportId() {
        return reportId;
    }
    public void setReportId(String reportId) {
        this.reportId = reportId;
    }
    public String getAmbulanceId() {
        return ambulanceId;
    }
    public void setAmbulanceId(String ambulanceId) {
        this.ambulanceId = ambulanceId;
    }
    public String getDriverId() {
        return driverId;
    }
    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }
    public String getAssignedBy() {
        return assignedBy;
    }
    public void setAssignedBy(String assignedBy) {
        this.assignedBy = assignedBy;
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
    public String getLatitude() {
        return latitude;
    }
    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }
    public String getLongitude() {
        return longitude;
    }
    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
    public String getDropLocation() {
        return dropLocation;
    }
    public void setDropLocation(String dropLocation) {
        this.dropLocation = dropLocation;
    }
    public String getHospitalId() {
        return hospitalId;
    }
    public void setHospitalId(String hospitalId) {
        this.hospitalId = hospitalId;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
   

}
