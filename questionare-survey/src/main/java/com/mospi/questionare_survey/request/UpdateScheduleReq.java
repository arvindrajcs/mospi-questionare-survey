package com.mospi.questionare_survey.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateScheduleReq {
    private Long scheduleId; // Required for updates
    private Long surveyId;
    private Long scheduleTypeId;
    private Long scheduleAttributesId;
}
