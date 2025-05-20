package org.irs.service;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.irs.client.AccidentInsightsClient;
import org.irs.dto.GenerateInsightsRequestDTO;
import org.irs.dto.JsonInsightsRequestDTO;
import org.irs.util.ConstantValues;
import org.irs.util.GeneralMethods;


@ApplicationScoped
public class AccidentInsightsService {

    @Inject
    @RestClient
    AccidentInsightsClient accidentInsightsClient;

    @Inject
    GeneralMethods generalMethods;

    public Object getJsonInsights(JsonInsightsRequestDTO request) {
        return accidentInsightsClient.getJsonInsights(request);
    }

    public Object askQuestion(GenerateInsightsRequestDTO request) {
        return accidentInsightsClient.askQuestion(request);
    }

    public Object getSqlInsights(GenerateInsightsRequestDTO request) {
        return accidentInsightsClient.getSqlInsights(request);
    }

    public Object getGenerateInsights(GenerateInsightsRequestDTO request) throws Exception{
        System.out.println("requested insights");
        if(request.getSql()!=null){
            return accidentInsightsClient.getSqlInsights(request);
        }else if(request.getQuestion()!=null){
            String sql = generalMethods.routeQuery(
            ConstantValues.QUESTION_SQL_MAP, 
            request.getQuestion()
            );
            if(sql!=null){
                request.setSql(sql);
                return accidentInsightsClient.getSqlInsights(request);
            }
            return accidentInsightsClient.askQuestion(request);
        }
        throw new Exception("Error Invalid Input ");
    }

} 