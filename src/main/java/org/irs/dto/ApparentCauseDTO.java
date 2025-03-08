package org.irs.dto;

public class ApparentCauseDTO {
    public String id;
    public String cause;
    public String otherDetails;

    public ApparentCauseDTO(String id, String cause, String otherDetails) {
        this.id = id;
        this.cause = cause;
        this.otherDetails = otherDetails;
    }

    // Getters and Setters
}
