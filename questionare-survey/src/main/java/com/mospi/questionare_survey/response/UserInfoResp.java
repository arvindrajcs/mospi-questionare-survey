package com.mospi.questionare_survey.response;

import lombok.*;


@Data
@NoArgsConstructor
@ToString
@AllArgsConstructor
public class UserInfoResp {
    private Long userId;
    private String username;
    private String passwordHash;
    private String firstName;
    private String lastName;
    private String email;
    private String mobileNumber;
    private Boolean isActive;


}
