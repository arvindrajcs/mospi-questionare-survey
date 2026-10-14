package com.mospi.questionare_survey.response;

import com.mospi.questionare_survey.entity.UserInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VerifyTokenResponse {

    private UserInfo user;
}
