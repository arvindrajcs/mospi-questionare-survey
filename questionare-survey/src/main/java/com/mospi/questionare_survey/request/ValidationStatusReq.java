package com.mospi.questionare_survey.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ValidationStatusReq {
    private Long validationId;
    private Boolean active;
}
