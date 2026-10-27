package com.mospi.questionare_survey.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TemplateResp {
    private Long templateId;
    private String templateName;
    private String templateType;
    private Long surveyId;
    private boolean isActive;
    private Long createdBy;
}
