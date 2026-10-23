package com.mospi.questionare_survey.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GetScheduleReq {
    private Long scheduleId;
    private Long surveyId;
    private Long userId;
    private Long page;
    private Long limit;
}
