package org.irs.dto;

public class VehicleInvolvedDTO {
    public String id;
    public String label;
    public String Description;

    public VehicleInvolvedDTO(String id, String Description, String label) {
        this.id = id;
        this.Description = Description;
        this.label = label;
    }

    @Override
    public String toString() {
        return "VehicleInvolvedDTO [id=" + id + ", label=" + label + ", Description=" + Description + "]";
    }


}
