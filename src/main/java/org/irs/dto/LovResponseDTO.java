package org.irs.dto;

import java.util.List;

public class LovResponseDTO {
    public List<GeneralLovDto> accidentTypes;
    public List<GeneralLovDto> vehicleInvolved;
    public List<GeneralLovDto> patientVictims;
    public List<ApparentCauseDTO> apparentCauses;
    public List<WeatherConditionDTO> weatherConditions;
    public List<VisibilityDTO> visibilityLevels;
    public List<RoadSurfaceConditionDTO> roadSurfaceConditions;
    public List<RoadTypeDTO> roadTypes;
    public List<RoadSignageDTO> roadSignages;
    public List<CaseReferredToDTO> caseReferredTo;
    public List<FaultAssessmentDTO> faultAssessments;
    public List<GeneralLovDto> genderTypes;
    public List<GeneralLovDto> vehicleConditions;
    public List<GeneralLovDto> fitnessCertificateStatuses;
    public List<GeneralLovDto> casualtiesStatuses;
    public List<GeneralLovDto> injurySeverities;
    public List<GeneralLovDto> roadTaxStatuses;
    public List<GeneralLovDto> insuranceStatuses;

    public LovResponseDTO(List<GeneralLovDto> accidentTypes, List<GeneralLovDto> vehicleInvolved,
            List<GeneralLovDto> patientVictims, List<ApparentCauseDTO> apparentCauses,
            List<WeatherConditionDTO> weatherConditions, List<VisibilityDTO> visibilityLevels,
            List<RoadSurfaceConditionDTO> roadSurfaceConditions, List<RoadTypeDTO> roadTypes,
            List<RoadSignageDTO> roadSignages, List<CaseReferredToDTO> caseReferredTo,
            List<FaultAssessmentDTO> faultAssessments, List<GeneralLovDto> genderTypes,
            List<GeneralLovDto> vehicleConditions,
            List<GeneralLovDto> fitnessCertificateStatuses,
            List<GeneralLovDto> casualtiesStatuses,
            List<GeneralLovDto> injurySeverities,
            List<GeneralLovDto> roadTaxStatuses,
            List<GeneralLovDto> insuranceStatuses) {
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
        this.genderTypes = genderTypes;
        this.vehicleConditions = vehicleConditions;
        this.fitnessCertificateStatuses = fitnessCertificateStatuses;
        this.casualtiesStatuses = casualtiesStatuses;
        this.injurySeverities = injurySeverities;
        this.roadTaxStatuses = roadTaxStatuses;
        this.insuranceStatuses = insuranceStatuses;
    }

    // Getters and Setters
}
