package com.mospi.questionare_survey.request;

import lombok.*;
@Data
@NoArgsConstructor
@ToString
@AllArgsConstructor
@Builder
public class ResetPasswordReq {
    private String empCode;
    private String otp;
    private String newPassword;
    private String reEnterPassword;
}
