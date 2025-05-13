package org.irs.dto;


public class PassengerDTO {
    public Integer type;
    public String name;
    public String hospitalName;
    public Integer injurySeverity;
    public Integer getType() {
        return type;
    }
    public void setType(Integer type) {
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
    public Integer getInjurySeverity() {
        return injurySeverity;
    }
    public void setInjurySeverity(Integer injurySeverity) {
        this.injurySeverity = injurySeverity;
    }
    @Override
    public String toString() {
        return "PassengerDTO [type=" + type + ", name=" + name + ", hospitalName=" + hospitalName + ", injurySeverity="
                + injurySeverity + "]";
    }

}
