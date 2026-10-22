package com.mospi.questionare_survey.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class BlockResp {
    private Long blockId;
    private Long surveyId;
    private Long scheduleId;
    private String blockHeading;
    private String blockCode;
    private Long statusId;
    private String statusDescription;
    private boolean active;
    private Long createdBy;
}
