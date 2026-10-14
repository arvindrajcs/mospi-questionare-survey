package com.mospi.questionare_survey.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = false)
public class UserStatusRequest {
    private Long userId; //Optional
    private String loginId;
    private String docPath;
    private Long flag;

}
