package com.mospi.questionare_survey.response;

import com.mospi.questionare_survey.request.SkipConditionReq;
import lombok.*;

import java.util.ArrayList;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TestSurveyResp {
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

    private ArrayList<TemplateResp> templateResp;
    private ArrayList<ScheduleResp> scheduleResp;
    private ArrayList<BlockResp> blockResp;
    private ArrayList<QuestionResp> questionResp;

}
