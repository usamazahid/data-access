package org.irs.dto;

import java.util.List;

public class LovResponseDTO {
    public List<AccidentTypesDTO> accidentTypes;
    public List<VehicleInvolvedDTO> vehicleInvolved;
    public List<PatientVictimDTO> patientVictims;
    public List<ApparentCauseDTO> apparentCauses;
    public List<WeatherConditionDTO> weatherConditions;
    public List<VisibilityDTO> visibilityLevels;
    public List<RoadSurfaceConditionDTO> roadSurfaceConditions;
    public List<RoadTypeDTO> roadTypes;
    public List<RoadSignageDTO> roadSignages;
    public List<CaseReferredToDTO> caseReferredTo;
    public List<FaultAssessmentDTO> faultAssessments;
    public List<GenderTypesDTO> genderTypes;

    public LovResponseDTO(List<AccidentTypesDTO> accidentTypes, List<VehicleInvolvedDTO> vehicleInvolved,
                          List<PatientVictimDTO> patientVictims, List<ApparentCauseDTO> apparentCauses,
                          List<WeatherConditionDTO> weatherConditions, List<VisibilityDTO> visibilityLevels,
                          List<RoadSurfaceConditionDTO> roadSurfaceConditions, List<RoadTypeDTO> roadTypes,
                          List<RoadSignageDTO> roadSignages, List<CaseReferredToDTO> caseReferredTo,
                          List<FaultAssessmentDTO> faultAssessments,List<GenderTypesDTO> genderTypes) {
        this.accidentTypes = accidentTypes;
        this.vehicleInvolved = vehicleInvolved;
        this.patientVictims = patientVictims;
        this.apparentCauses = apparentCauses;
        this.weatherConditions = weatherConditions;
        this.visibilityLevels = visibilityLevels;
        this.roadSurfaceConditions = roadSurfaceConditions;
        this.roadTypes = roadTypes;
        this.roadSignages = roadSignages;
        this.caseReferredTo = caseReferredTo;
        this.faultAssessments = faultAssessments;
        this.genderTypes=genderTypes;
    }

    // Getters and Setters
}

