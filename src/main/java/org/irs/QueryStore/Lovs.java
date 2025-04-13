package org.irs.QueryStore;

import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class Lovs {

    public String getAccidentTypes(){
        String query = "select * from accident_types";
        // System.out.println(query);
        return query;
    }

    public String getOrganizations(){
        String query = "select * from organizations where has_ambulance_service=TRUE";
        // System.out.println(query);
        return query;
    }

    public String getVehicleInvolved(){
        String query = "select * from vehicle_involved";
        // System.out.println(query);
        return query;
    }

    public String getPatientVictim(){
        String query = "select * from patient_victim";
        // System.out.println(query);
        return query;
    }
    
    public String getApparentCause() {
        String query = "SELECT * FROM apparent_cause";
        return query;
    }
    
    public String getWeatherCondition() {
        String query = "SELECT * FROM weather_condition";
        return query;
    }
    
    public String getVisibility() {
        String query = "SELECT * FROM visibility";
        return query;
    }
    
    public String getRoadSurfaceCondition() {
        String query = "SELECT * FROM road_surface_condition";
        return query;
    }
    
    public String getRoadType() {
        String query = "SELECT * FROM road_type";
        return query;
    }
    
    public String getRoadSignage() {
        String query = "SELECT * FROM road_signage";
        return query;
    }
    
    public String getCaseReferredTo() {
        String query = "SELECT * FROM case_referred_to";
        return query;
    }
    
    public String getFaultAssessment() {
        String query = "SELECT * FROM preliminary_fault_assessment";
        return query;
    }

    public String getGenderTypes() {
        String query = "SELECT * FROM gender_types";
        return query;
    }
}
