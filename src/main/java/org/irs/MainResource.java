package org.irs;

import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.Map;
import org.apache.commons.math3.ml.clustering.Cluster;
import org.apache.commons.math3.ml.clustering.DoublePoint;
import org.irs.dto.AccidentReportRequestDTO;
import org.irs.dto.AccidentReportResponseDTO;
import org.irs.dto.AccidentStatisticsDTO;
import org.irs.dto.ApparentCauseDTO;
import org.irs.dto.CaseReferredToDTO;
import org.irs.dto.DispatchRequestDto;
import org.irs.dto.ErrorResponse;
import org.irs.dto.FaultAssessmentDTO;
import org.irs.dto.GeneralLovDto;
import org.irs.dto.GenerateInsightsRequestDTO;
import org.irs.dto.JsonInsightsRequestDTO;
import org.irs.dto.LovResponseDTO;
import org.irs.dto.OrganizationsDTO;
import org.irs.dto.RequestDto;
import org.irs.dto.RoadSignageDTO;
import org.irs.dto.RoadSurfaceConditionDTO;
import org.irs.dto.RoadTypeDTO;
import org.irs.dto.StatisticsRequestDTO;
import org.irs.dto.UserRequestDTO;
import org.irs.dto.UserResponseDTO;
import org.irs.dto.UserRolesDTO;
import org.irs.dto.VisibilityDTO;
import org.irs.dto.WeatherConditionDTO;
import org.irs.service.AccidentInsightsService;
import org.irs.service.AccidentReportService;
import org.irs.service.ClusterEvaluationService;
import org.irs.service.DispatchService;
import org.irs.service.LovService;
import org.irs.service.UserDetailService;


@Path("/irs")
public class MainResource {

    @Inject
    UserDetailService userDetailService;

    @Inject
    LovService lovService;

    @Inject
    AccidentReportService accidentReportService;

    @Inject
    AccidentInsightsService accidentInsightsService;

    @Inject
    ClusterEvaluationService clusterEvaluationService;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/getUserData")
    public UserResponseDTO getUserData(UserRequestDTO request) {
        UserResponseDTO response = userDetailService.getUserData(request);
        return response;
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/getUserRoles")
    public List<UserRolesDTO> getUserRoles(UserRequestDTO request) {
        return userDetailService.getUserRoles(request);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/getAccidentTypes")
    public List<GeneralLovDto> getAccidentTypes() {
        return lovService.getAccidentTypes();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/getOrganizations")
    public List<OrganizationsDTO> getOrganizations() {
        return lovService.getOrganizations();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/getVehicleInvolved")
    public List<GeneralLovDto> getVehicleInvolved() {
        return lovService.getVehicleInvolved();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/getPatientVictim")
    public List<GeneralLovDto> getPatientVictim() {
        return lovService.getPatientVictim();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/saveReportData")
    public AccidentReportResponseDTO saveReportData(AccidentReportRequestDTO request) {
        AccidentReportResponseDTO response = accidentReportService.saveAccidentReport(request);
        return response;
    }

    @GET
    @Path("/getReportByUserId/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<AccidentReportResponseDTO> getAccidentReportsByUserId(@PathParam("userId") String userId) {
        return accidentReportService.getAccidentReportsByUserId(userId);

    }

    @GET
    @Path("getReportById/{reportId}")
    @Produces(MediaType.APPLICATION_JSON)
    public AccidentReportResponseDTO getAccidentReport(@PathParam("reportId") String reportId) {
        return accidentReportService.getAccidentReportById(reportId);
    }

    @GET
    @Path("/getJoinedReportByUserId/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, Object> getJoinedAccidentReportsByUserId(@PathParam("userId") String userId,@QueryParam("pageNumber") Integer pageNumber,@QueryParam("recordsPerPage") Integer recordsPerPage) {
        return accidentReportService.getJoinedAccidentReportsByUserId(userId,pageNumber,recordsPerPage);

    }


    @GET
    @Path("getJoinedReportById/{reportId}")
    @Produces(MediaType.APPLICATION_JSON)
    public AccidentReportResponseDTO getJoinedAccidentReport(@PathParam("reportId") String reportId,@QueryParam("isFile") Boolean isFile) {
        boolean sendFile=true;
        if(isFile!=null){
            sendFile=isFile;
        }
        return accidentReportService.getJoinedAccidentReportById(reportId,sendFile);
    }

    @GET
    @Path("heatmap")
    @Produces(MediaType.APPLICATION_JSON)
    public List<AccidentReportResponseDTO> getAccidentHeatmapData(RequestDto requestDto) {
        return accidentReportService.getAccidentHeatmapData(requestDto);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/createUser")
    public Response createUser(UserRequestDTO request) {
        try {
            userDetailService.createUser(request);
            return Response.status(Response.Status.CREATED).entity("User registered successfully").build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("FAILED").build();
        }

    }

    @GET
    @Path("/base64/{filename}")
    public String getImageBase64(@PathParam("filename") String filename) {
        return accidentReportService.getFileBase64Response(filename);
    }

    @GET
    @Path("getReportFiles/{reportId}")
    @Produces(MediaType.APPLICATION_JSON)
    public AccidentReportResponseDTO getFileData(@PathParam("reportId") String reportId) {
        return accidentReportService.getReportFiles(reportId);
    }

    @Inject
    DispatchService dispatchService;

    // 1. Create a new dispatch
    @POST
    @Path("dispatch/create")
    public Response createDispatch(DispatchRequestDto dispatchDTO) {
        try {
            return dispatchService.createDispatch(dispatchDTO);
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error creating dispatch: " + e.getMessage())
                    .build();
        }
    }

    // 2. Get dispatches by driver
    @GET
    @Path("dispatch/driver/{driverId}")
    public Response getDispatchesByDriver(@PathParam("driverId") Integer driverId) {
        try {
            return dispatchService.getDispatchesByDriver(driverId);
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error retrieving dispatches: " + e.getMessage())
                    .build();
        }
    }

    // 3. Accept a dispatch
    @PUT
    @Path("dispatch/accept")
    public Response acceptDispatch(DispatchRequestDto dispatchRequestDto) {
        try {
            dispatchService.acceptDispatch(dispatchRequestDto.dispatchId, dispatchRequestDto.ambulanceId,
                    dispatchRequestDto.reportId);
            return Response.ok("Dispatch accepted successfully").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error accepting dispatch: " + e.getMessage())
                    .build();
        }
    }

    // 4. Update pickup time
    @PUT
    @Path("dispatch/pickup/{dispatchId}")
    public Response updatePickup(@PathParam("dispatchId") Integer dispatchId) {
        try {
            return dispatchService.updatePickup(dispatchId);
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error updating pickup time: " + e.getMessage())
                    .build();
        }
    }

    // 5. Update drop details
    @PUT
    @Path("dispatch/drop")
    public Response updateDrop(DispatchRequestDto dispatchRequestDto) {
        try {
            return dispatchService.updateDrop(dispatchRequestDto);
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error updating drop details: " + e.getMessage())
                    .build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/getApparentCause")
    public List<ApparentCauseDTO> getApparentCauseEndpoint() {
        return lovService.getApparentCause();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/getWeatherCondition")
    public List<WeatherConditionDTO> getWeatherConditionEndpoint() {
        return lovService.getWeatherCondition();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/getVisibility")
    public List<VisibilityDTO> getVisibilityEndpoint() {
        return lovService.getVisibility();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/getRoadSurfaceCondition")
    public List<RoadSurfaceConditionDTO> getRoadSurfaceConditionEndpoint() {
        return lovService.getRoadSurfaceCondition();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/getRoadType")
    public List<RoadTypeDTO> getRoadTypeEndpoint() {
        return lovService.getRoadType();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/getRoadSignage")
    public List<RoadSignageDTO> getRoadSignageEndpoint() {
        return lovService.getRoadSignage();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/getCaseReferredTo")
    public List<CaseReferredToDTO> getCaseReferredToEndpoint() {
        return lovService.getCaseReferredTo();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/getFaultAssessment")
    public List<FaultAssessmentDTO> getFaultAssessmentEndpoint() {
        return lovService.getFaultAssessment();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/getAllLovs")
    public Map<String, List<Map<String, Object>>> getAllLovs() {
        return lovService.getAllLovs();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/fetchAllLovs")
    public LovResponseDTO fetchAllLovs() {
        return lovService.fetchAllLovs();
    }

    @GET
    @Path("/getClusteredAccidentsDBSCAN")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getClusteredAccidentsDBSCAN(RequestDto requestDto) {
        try {
            List<Map<String, Object>> clusters = accidentReportService.getClusteredAccidentsDBSCAN(requestDto);
            return Response.ok(clusters).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error fetching clustered accidents")
                    .build();
        }
    }

    @GET
    @Path("/getClusteredAccidents")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getClusteredAccidents(RequestDto requestDto) {
        try {
            List<Cluster<DoublePoint>> clusters = accidentReportService.getClusteredAccidents(requestDto);
            return Response.ok(clusters).build();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error fetching clustered accidents")
                    .build();
        }
    }

    @POST
    @Path("/statistics/overview")
    public Response getStatistics(StatisticsRequestDTO request) {
        try {
            if(request==null){request=new StatisticsRequestDTO();}
            AccidentStatisticsDTO statistics = accidentReportService.getAccidentStatistics(request);
            return Response.ok(statistics).build();
        } catch (Exception e) {
            return Response.serverError()
                         .entity(new ErrorResponse("Error fetching statistics: " + e.getMessage()))
                         .build();
        }
    }

    @POST
    @Path("/accident-insights/json")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getJsonInsights(JsonInsightsRequestDTO request) {
        try {
            Object response = accidentInsightsService.getJsonInsights(request);
            return Response.ok(response).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Error getting JSON insights: " + e.getMessage()))
                    .build();
        }
    }

    @POST
    @Path("/accident-insights/ask")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response askQuestion(GenerateInsightsRequestDTO request) {
        try {
            Object response = accidentInsightsService.askQuestion(request);
            return Response.ok(response).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Error asking question: " + e.getMessage()))
                    .build();
        }
    }

    @POST
    @Path("/accident-insights/sql")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getSqlInsights(GenerateInsightsRequestDTO request) {
        try {
            Object response = accidentInsightsService.getSqlInsights(request);
            return Response.ok(response).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Error getting SQL insights: " + e.getMessage()))
                    .build();
        }
    }


    @POST
    @Path("/accident-insights/generate")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getGenerateInsights(GenerateInsightsRequestDTO request) {
        try {
            Object response = accidentInsightsService.getGenerateInsights(request);
            return Response.ok(response).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ErrorResponse("Error getting SQL insights: " + e.getMessage()))
                    .build();
        }
    }

    @GET
    @Path("/evaluateClustering")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response evaluateClustering(RequestDto requestDto) {
        try {
            Map<String, Object> evaluationResults = clusterEvaluationService.evaluateClustering(requestDto);
            return Response.ok(evaluationResults).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(Map.of("error", e.getMessage()))
                .build();
        }
    }

}
