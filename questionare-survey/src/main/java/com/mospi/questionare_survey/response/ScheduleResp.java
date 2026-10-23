package com.mospi.questionare_survey.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleResp {
    private Long scheduleId;
    private Long surveyId;
    private Long scheduleTypeId;
    private String scheduleTypeDescription; // Optional: include master description if needed
    private Long scheduleAttributesId;
    private String scheduleAttributesDescription; // Optional
    private Long statusId;
    private String statusDescription;
    private boolean isActive;
    private Long createdBy;
}
