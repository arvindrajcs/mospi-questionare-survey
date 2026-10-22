package com.mospi.questionare_survey.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateSurveyReq {

    private Long surveyId; // Required for updates
    private String title;
    private String periodicCycle;
    private Long divisionId;
    private Long enquiryTypeId;
    private Long frequencyTypeId;
    private Long seasonalityFactorId;
    private String surveyDuration;
    private String surveyCode;
    private String ruralSamplingFrame;
    private String urbanSamplingFrame;
    private String samplingSize;
    private String surveyObjective;
    private String remarks;
}
