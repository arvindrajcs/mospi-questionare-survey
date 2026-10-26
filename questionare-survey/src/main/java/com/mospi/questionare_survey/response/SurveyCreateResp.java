package com.mospi.questionare_survey.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SurveyCreateResp {
    private String msg;
    private Long surveyId;
}
