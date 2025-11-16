package org.irs.dto;


public class PassengerDTO {
    public String type;
    public String name;
    public String hospitalName;
    public String injurySeverity;
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getHospitalName() {
        return hospitalName;
    }
    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }
    public String getInjurySeverity() {
        return injurySeverity;
    }
    public void setInjurySeverity(String injurySeverity) {
        this.injurySeverity = injurySeverity;
    }
    @Override
    public String toString() {
        return "PassengerDTO [type=" + type + ", name=" + name + ", hospitalName=" + hospitalName + ", injurySeverity="
                + injurySeverity + "]";
    }

}
