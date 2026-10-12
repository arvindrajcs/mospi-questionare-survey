package com.mospi.questionare_survey.response;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class StateResponse {
    private Long id;

    private String stateCode;

    private String stateName;

    private String status;

    private String lastChgBy;

    private Instant lastChgDate;

    private String lastChgTime;

    private String hicCode;

    private long countryId;
}
