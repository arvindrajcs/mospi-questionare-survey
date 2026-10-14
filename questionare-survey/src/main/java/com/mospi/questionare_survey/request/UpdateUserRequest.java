package com.mospi.questionare_survey.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = false)
public class UpdateUserRequest {
    private Long userId;
    private String firstName;
    private String lastName;
    private Long roleId;
    private String email;
    private String mobile;
    private String remarks;
    private String docPath;

}
