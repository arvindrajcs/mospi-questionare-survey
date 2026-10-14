package com.mospi.questionare_survey.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = false)
public class CreateUserRequest {

    private String firstName;
    private String lastName;
    private Long roleId;

    @NotBlank(message = "Login Id Can Not be Null or empty")
    private String loginId;  //encrypted

    @NotBlank(message = "User Password Can Not be Null or empty")
    private String password; //encrypted

    private String email;
    private String mobile;
    private String remarks;
    private String docPath;
    private Long orgId;
    private String orgName;
    private String orgType;

//    @NotBlank(message = "AES Key Can Not Be Null or empty")
//    private String key; //new for encryption

}
