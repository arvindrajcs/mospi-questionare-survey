package com.mospi.questionare_survey.jwt;

import com.mospi.questionare_survey.response.RoleInfoResp;
import lombok.*;

import java.util.ArrayList;

@Getter
@Setter
@NoArgsConstructor
@ToString
@AllArgsConstructor
@Builder
public class JwtResponce {

    private String jwtToken;
    private String username;
    private ArrayList<RoleInfoResp> role;

}
