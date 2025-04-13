package org.irs.dto;

public class GenderTypesDTO {
    public String id;
    public String label;
    public String description;

    public GenderTypesDTO(String id, String label, String description) {
        this.id = id;
        this.description = description;
        this.label = label;
    }

    @Override
    public String toString() {
        return "GenderTypesDTO [id=" + id + ", label=" + label + ", description=" + description + "]";
    }
}
