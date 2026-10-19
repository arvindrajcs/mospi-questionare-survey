package com.mospi.questionare_survey.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = false)
public class CreateSurveyReq {

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
