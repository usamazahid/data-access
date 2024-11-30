package org.irs.dto;

public class PatientVictimDTO {
    public String id;
    public String label;
    public String description;

    public PatientVictimDTO(String id, String description, String label) {
        this.description = description;
        this.label = label;
    }


}
