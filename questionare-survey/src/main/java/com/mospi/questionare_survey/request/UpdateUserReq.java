package com.mospi.questionare_survey.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = false)
public class UpdateUserReq {
    private Long userId;
    private String passwordHash;
    private String firstName;
    private String lastName;
    private String email;
    private String mobileNumber;

}
