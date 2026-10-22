package com.mospi.questionare_survey.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateBlockReq {
    private Long blockId; // Required for updates
    private Long surveyId;
    private Long scheduleId;
    private String blockHeading;
    private String blockCode;
}
