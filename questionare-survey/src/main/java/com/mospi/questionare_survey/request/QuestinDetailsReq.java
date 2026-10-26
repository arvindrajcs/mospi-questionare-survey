package com.mospi.questionare_survey.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = false)
public class QuestinDetailsReq {
    private Long surveyId;
    private Long blockId;
    private String questionNumber;
    private String fieldInstructions;
    private String questionHeading;
    private String dataFormate;
    private String dropDownValue;
    private boolean isMandatory;
    private String questionCondition;

    private ArrayList<SkipConditionReq> skipConditionReq;
    private ArrayList<QuestionValidationReq> questionValidationReq;
}
