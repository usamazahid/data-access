package org.irs.dto;

public class PatientVictimDTO {
    public String id;
    public String label;
    public String description;

    public PatientVictimDTO(String id, String description, String label) {
        this.id = id;
        this.description = description;
        this.label = label;
    }

    @Override
    public String toString() {
        return "PatientVictimDTO [id=" + id + ", label=" + label + ", description=" + description + "]";
    }


}
