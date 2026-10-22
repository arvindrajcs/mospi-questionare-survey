package com.mospi.questionare_survey.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuestionValidationReq {
    private Long validationId; // Required for updates
    private String validationType;
    private String severityCheck;
    private String value;
    private String errorMsg;
}
