package org.irs.dto;

public class AccidentTypesDTO {
    public String id;
    public String label;
    public String description;

    public AccidentTypesDTO(String id, String label, String description) {
        this.id = id;
        this.description = description;
        this.label = label;
    }

    @Override
    public String toString() {
        return "AccidentTypesDTO [id=" + id + ", label=" + label + ", description=" + description + "]";
    }
}
