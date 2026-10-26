package com.mospi.questionare_survey.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleCreationResp {
    private String msg;
    private Long scheduleId;
}
