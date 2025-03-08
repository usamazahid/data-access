package org.irs.service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.irs.QueryStore.Lovs;
import org.irs.database.Datasources;
import org.irs.dto.AccidentTypesDTO;
import org.irs.dto.ApparentCauseDTO;
import org.irs.dto.CaseReferredToDTO;
import org.irs.dto.FaultAssessmentDTO;
import org.irs.dto.ImageDTO;
import org.irs.dto.LovResponseDTO;
import org.irs.dto.OrganizationsDTO;
import org.irs.dto.PatientVictimDTO;
import org.irs.dto.RoadSignageDTO;
import org.irs.dto.RoadSurfaceConditionDTO;
import org.irs.dto.RoadTypeDTO;
import org.irs.dto.VehicleInvolvedDTO;
import org.irs.dto.VisibilityDTO;
import org.irs.dto.WeatherConditionDTO;

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

    public List<RoadTypeDTO> getRoadType() {
        List<RoadTypeDTO> response = new ArrayList<>();
        String query = lov.getRoadType();
        try (Connection con = datasource.getConnection(); Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                response.add(new RoadTypeDTO(rs.getString("id"), rs.getString("type")));
            }
        } catch (Exception ex) {
            // Handle exception
        }
        return response;
    }

    public List<RoadSignageDTO> getRoadSignage() {
        List<RoadSignageDTO> response = new ArrayList<>();
        String query = lov.getRoadSignage();
        try (Connection con = datasource.getConnection(); Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                response.add(new RoadSignageDTO(rs.getString("id"), rs.getString("status")));
            }
        } catch (Exception ex) {
            // Handle exception
        }
        return response;
    }

    public List<CaseReferredToDTO> getCaseReferredTo() {
        List<CaseReferredToDTO> response = new ArrayList<>();
        String query = lov.getCaseReferredTo();
        try (Connection con = datasource.getConnection(); Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                response.add(new CaseReferredToDTO(rs.getString("id"), rs.getString("unit")));
            }
        } catch (Exception ex) {
            // Handle exception
        }
        return response;
    }

    public List<FaultAssessmentDTO> getFaultAssessment() {
        List<FaultAssessmentDTO> response = new ArrayList<>();
        String query = lov.getFaultAssessment();
        try (Connection con = datasource.getConnection(); Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                response.add(new FaultAssessmentDTO(rs.getString("id"), rs.getString("fault")));
            }
        } catch (Exception ex) {
            // Handle exception
        }
        return response;
    }
    public List<ApparentCauseDTO> getApparentCause() {
        List<ApparentCauseDTO> response = new ArrayList<>();
        String query = lov.getApparentCause();
        try (Connection con = datasource.getConnection(); Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                response.add(new ApparentCauseDTO(rs.getString("id"), rs.getString("cause"), rs.getString("other_details")));
            }
        } catch (Exception ex) {
            // Handle exception
        }
        return response;
    }

    public List<WeatherConditionDTO> getWeatherCondition() {
        List<WeatherConditionDTO> response = new ArrayList<>();
        String query = lov.getWeatherCondition();
        try (Connection con = datasource.getConnection(); Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                response.add(new WeatherConditionDTO(rs.getString("id"), rs.getString("condition")));
            }
        } catch (Exception ex) {
            // Handle exception
        }
        return response;
    }

    public List<VisibilityDTO> getVisibility() {
        List<VisibilityDTO> response = new ArrayList<>();
        String query = lov.getVisibility();
        try (Connection con = datasource.getConnection(); Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                response.add(new VisibilityDTO(rs.getString("id"), rs.getString("level")));
            }
        } catch (Exception ex) {
            // Handle exception
        }
        return response;
    }
    public List<RoadSurfaceConditionDTO> getRoadSurfaceCondition() {
        List<RoadSurfaceConditionDTO> response = new ArrayList<>();
        String query = lov.getRoadSurfaceCondition();
        try (Connection con = datasource.getConnection(); Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                response.add(new RoadSurfaceConditionDTO(rs.getString("id"), rs.getString("condition")));
            }
        } catch (Exception ex) {
            // Handle exception
        }
        return response;
    }


    public Map<String, List<Map<String, Object>>> getAllLovs() {
        Map<String, List<Map<String, Object>>> lovResponse = new HashMap<>();

        // Define lookup tables
        String[] tables = {
            "accident_types", "vehicle_involved", "patient_victim", 
            "apparent_cause", "weather_condition", "visibility", 
            "road_surface_condition", "road_type", "road_signage", 
            "case_referred_to", "preliminary_fault_assessment"
        };

        try (Connection con = datasource.getConnection();
            Statement stmt = con.createStatement()) {

            for (String table : tables) {
                String query = "SELECT * FROM " + table;
                try (ResultSet rs = stmt.executeQuery(query)) {
                    List<Map<String, Object>> records = new ArrayList<>();
                    ResultSetMetaData metaData = rs.getMetaData();
                    int columnCount = metaData.getColumnCount();

                    while (rs.next()) {
                        Map<String, Object> row = new HashMap<>();
                        for (int i = 1; i <= columnCount; i++) {
                            row.put(metaData.getColumnName(i), rs.getObject(i));
                        }
                        records.add(row);
                    }

                    lovResponse.put(table, records);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return lovResponse;
    }



    public LovResponseDTO fetchAllLovs() {
    LovResponseDTO response = new LovResponseDTO(
        Optional.ofNullable(getAccidentTypes()).orElse(new ArrayList<>()),
        Optional.ofNullable(getVehicleInvolved()).orElse(new ArrayList<>()),
        Optional.ofNullable(getPatientVictim()).orElse(new ArrayList<>()),
        Optional.ofNullable(getApparentCause()).orElse(new ArrayList<>()),
        Optional.ofNullable(getWeatherCondition()).orElse(new ArrayList<>()),
        Optional.ofNullable(getVisibility()).orElse(new ArrayList<>()),
        Optional.ofNullable(getRoadSurfaceCondition()).orElse(new ArrayList<>()),
        Optional.ofNullable(getRoadType()).orElse(new ArrayList<>()),
        Optional.ofNullable(getRoadSignage()).orElse(new ArrayList<>()),
        Optional.ofNullable(getCaseReferredTo()).orElse(new ArrayList<>()),
        Optional.ofNullable(getFaultAssessment()).orElse(new ArrayList<>())
    );
    return response;
    }
}
