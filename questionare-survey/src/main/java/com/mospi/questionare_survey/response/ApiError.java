package com.mospi.questionare_survey.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;


@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiError {

    private String[] field;
    private String message;

}
