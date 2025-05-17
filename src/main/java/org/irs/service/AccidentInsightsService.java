package org.irs.service;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.irs.client.AccidentInsightsClient;
import org.irs.dto.AskRequestDTO;
import org.irs.dto.JsonInsightsRequestDTO;
import org.irs.dto.SqlInsightsRequestDTO;

@ApplicationScoped
public class AccidentInsightsService {

    @RestClient
    AccidentInsightsClient accidentInsightsClient;

    public Object getJsonInsights(JsonInsightsRequestDTO request) {
        return accidentInsightsClient.getJsonInsights(request);
    }

    public Object askQuestion(AskRequestDTO request) {
        return accidentInsightsClient.askQuestion(request);
    }

    public Object getSqlInsights(SqlInsightsRequestDTO request) {
        return accidentInsightsClient.getSqlInsights(request);
    }
} 