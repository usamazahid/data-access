package org.irs.client;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.irs.dto.GenerateInsightsRequestDTO;
import org.irs.dto.JsonInsightsRequestDTO;

@Path("/api")
@RegisterRestClient(configKey = "accident-insights-api")
public interface AccidentInsightsClient {
    
    @POST
    @Path("/json-insights")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    Object getJsonInsights(JsonInsightsRequestDTO request);

    @POST
    @Path("/ask")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    Object askQuestion(GenerateInsightsRequestDTO request);

    @POST
    @Path("/sql-insights")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    Object getSqlInsights(GenerateInsightsRequestDTO request);
} 