package com.mospi.questionare_survey.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetTemplateReq {
    private Long templateId;
    private Long surveyId;
    private Long userId; // Optional: if filtering by user
    private Long page;
    private Long limit;
}
