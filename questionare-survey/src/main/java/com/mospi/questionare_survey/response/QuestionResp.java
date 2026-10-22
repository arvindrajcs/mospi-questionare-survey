package com.mospi.questionare_survey.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionResp {
    private Long questionId;
    private Long surveyId;
    private Long blockId;
    private String questionNumber;
    private String fieldInstructions;
    private String questionHeading;
    private String dataFormate;
    private String dropDownValue;
    private boolean isMandatory;

    private List<SkipConditionResp> skipConditionReq;
    private List<QuestionValidationResp> questionValidationReq;
}
