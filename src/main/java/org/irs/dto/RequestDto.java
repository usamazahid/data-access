package org.irs.dto;

import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;

public class RequestDto {
    @QueryParam("range") 
    public String range;
    @QueryParam("limit") 
    public Integer limit;
    @QueryParam("pageNumber")
     public Integer pageNumber;
    @QueryParam("recordsPerPage")
    public Integer recordsPerPage;
    @PathParam("userId")
    public String userId;
    @QueryParam("vehicleType")
    public String vehicleType;
    @QueryParam("accidentType")
    public String accidentType;
    @Override
    public String toString() {
        return "RequestDto [range=" + range + ", limit=" + limit + ", pageNumber=" + pageNumber + ", recordsPerPage="
                + recordsPerPage + ", userId=" + userId + ", vehicleType=" + vehicleType + ", accidentType="
                + accidentType + "]";
    }

}
