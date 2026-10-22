package com.mospi.questionare_survey.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionValidationResp {
    private Long validationId;
    private String validationType;
    private String severityCheck;
    private String value;
    private String errorMsg;
}
