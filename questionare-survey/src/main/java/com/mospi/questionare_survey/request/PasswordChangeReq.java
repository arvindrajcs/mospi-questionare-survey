package com.mospi.questionare_survey.request;

import lombok.*;

@Data
@NoArgsConstructor
@ToString
@AllArgsConstructor
@Builder
public class PasswordChangeReq {
    private String username;
    private String currentPassword;
    private String newPassword;
}
