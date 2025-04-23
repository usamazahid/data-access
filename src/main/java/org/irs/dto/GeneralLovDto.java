package org.irs.dto;

public class GeneralLovDto {
    public String id;
    public String label;
    public String description;

    public GeneralLovDto(String id, String label, String description) {
        this.id = id;
        this.description = description;
        this.label = label;
    }

    @Override
    public String toString() {
        return "GeneralLovDto [id=" + id + ", label=" + label + ", description=" + description + "]";
    }
}
