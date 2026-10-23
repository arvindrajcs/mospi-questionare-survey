package com.mospi.questionare_survey.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SurveyResp {
    private Long surveyId;
    private String title;
    private String periodicCycle;
    private String surveyCode;
    private Long divisionId;
    private String divisionDescription; // Optional
    private Long enquiryTypeId;
    private String enquiryTypeDescription; // Optional
    private Long frequencyTypeId;
    private String frequencyTypeDescription; // Optional
    private Long seasonalityFactorId;
    private String seasonalityFactorDescription; // Optional
    private String surveyDuration;
    private String ruralSamplingFrame;
    private String urbanSamplingFrame;
    private String samplingSize;
    private String surveyObjective;
    private String remarks;
    private Long statusId;
    private String statusDescription;
    private boolean isActive;
    private Long createdBy;
}
