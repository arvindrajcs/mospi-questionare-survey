package com.mospi.questionare_survey.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SkipConditionResp {
    private Long skipConditionId;
    private Long questionId; // Target question ID if applicable
    private String condition;
    private String value;
}
