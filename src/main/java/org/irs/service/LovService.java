package org.irs.service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.irs.QueryStore.Lovs;
import org.irs.database.Datasources;
import org.irs.dto.AccidentTypesDTO;
import org.irs.dto.ImageDTO;
import org.irs.dto.OrganizationsDTO;
import org.irs.dto.PatientVictimDTO;
import org.irs.dto.VehicleInvolvedDTO;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class LovService {

    @Inject
    Lovs lov;

    @Inject
    Datasources datasource;

    public List<AccidentTypesDTO> getAccidentTypes() {
        List<AccidentTypesDTO> response = new ArrayList<AccidentTypesDTO>();
        System.out.println("Getting Accident Types: ");
        String query = lov.getAccidentTypes();
        try (Connection con = datasource.getConnection(); Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                response.add(new AccidentTypesDTO(rs.getString("id"), rs.getString("label"), rs.getString("description")));
            }
        } catch (Exception ex) {

        }
        System.out.println(response.toString());
        return response;
    }

    public List<OrganizationsDTO> getOrganizations() {
        List<OrganizationsDTO> response = new ArrayList<OrganizationsDTO>();

        String query = lov.getOrganizations();
        try (Connection con = datasource.getConnection(); Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                response.add(new OrganizationsDTO(
                rs.getString("id"), 
                rs.getString("description"),
                new ImageDTO(rs.getString("image_uri")), 
                rs.getString("label"), 
                rs.getString("location"), 
                rs.getString("phone") ));
            }
        } catch (Exception ex) {

        }
        System.out.println("Organizations: " + response);
        return response;
    }

    public List<VehicleInvolvedDTO> getVehicleInvolved() {
        List<VehicleInvolvedDTO> response = new ArrayList<VehicleInvolvedDTO>();
        System.out.println("Getting Vehcile Involved.");
        String query = lov.getVehicleInvolved();
        try (Connection con = datasource.getConnection(); Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                response.add(new VehicleInvolvedDTO(
                    rs.getString("id"),     
                    rs.getString("description"), 
                    rs.getString("label"))); 
            }
        } catch (Exception ex) {

        }
        System.out.println("Vehicle Involved: " + response);

        return response;
    }

    public List<PatientVictimDTO> getPatientVictim() {
        List<PatientVictimDTO> response = new ArrayList<PatientVictimDTO>();
        System.out.println("Getting Patient victim.");
        String query = lov.getPatientVictim();
        try (Connection con = datasource.getConnection(); Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                response.add(new PatientVictimDTO(
                    rs.getString("id"), 
                    rs.getString("description"), 
                    rs.getString("label"))); 
            }
        } catch (Exception ex) {

        }
        System.out.println("Response Patient victim."+ response.toString());
        return response;
    }
}
