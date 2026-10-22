package com.mospi.questionare_survey.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateQuestionReq {
    private Long questionId; // Required for updates
    private Long surveyId;
    private Long blockId;
    private String questionNumber;
    private String fieldInstructions;
    private String questionHeading;
    private String dataFormate;
    private String dropDownValue;
    private boolean isMandatory;
    private ArrayList<SkipConditionReq> skipConditionReq;
    private ArrayList<QuestionValidationReq> questionValidationReq;
}
