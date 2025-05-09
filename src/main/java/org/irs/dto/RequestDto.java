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
    @QueryParam("startDate")
    public String startDate;
    @QueryParam("endDate")
    public String endDate;
    @QueryParam("severity")
    public String severity;
    @QueryParam("swLat")
    public Double swLat;
    
    @QueryParam("swLng")
    public Double swLng;



    @QueryParam("neLat")
    public Double neLat;

    @QueryParam("neLng")
    public Double neLng;

    @Override
    public String toString() {
        return "RequestDto [range=" + range + ", limit=" + limit + ", pageNumber=" + pageNumber + ", recordsPerPage="
                + recordsPerPage + ", userId=" + userId + ", vehicleType=" + vehicleType + ", accidentType="
                + accidentType + "]";
    }

}
