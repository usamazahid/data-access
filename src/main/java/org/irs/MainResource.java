package org.irs;

import java.util.List;

import org.irs.dto.AccidentTypesDTO;
import org.irs.dto.OrganizationsDTO;
import org.irs.dto.PatientVictimDTO;
import org.irs.dto.UserRequestDTO;
import org.irs.dto.UserResponseDTO;
import org.irs.dto.UserRolesDTO;
import org.irs.dto.VehicleInvolvedDTO;
import org.irs.service.LovService;
import org.irs.service.UserDetailService;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/irs")
public class MainResource {

    @Inject
    UserDetailService userDetailService;

    @Inject
    LovService lovService;
    

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/getUserData")
    public UserResponseDTO getUserData(UserRequestDTO request){
        UserResponseDTO response = userDetailService.getUserData(request);
        return response;
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/getUserRoles")
    public List<UserRolesDTO> getUserRoles(UserRequestDTO request){
        return userDetailService.getUserRoles(request);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/getAccidentTypes")
    public List<AccidentTypesDTO> getAccidentTypes(){
        return lovService.getAccidentTypes();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/getOrganizations")
    public List<OrganizationsDTO> getOrganizations(){
        return lovService.getOrganizations();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/getVehicleInvolved")
    public List<VehicleInvolvedDTO> getVehicleInvolved(){
        return lovService.getVehicleInvolved();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/getPatientVictim")
    public List<PatientVictimDTO> getPatientVictim(){
        return lovService.getPatientVictim();
    }
}
