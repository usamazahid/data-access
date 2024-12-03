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

}
