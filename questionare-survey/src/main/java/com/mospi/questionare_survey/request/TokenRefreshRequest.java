package com.mospi.questionare_survey.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = false)
public class TokenRefreshRequest {
    private String refreshToken;
    private String deviceInfo;
    private Long page;
    private Long limit;
}
