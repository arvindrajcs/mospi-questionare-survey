package com.mospi.questionare_survey.response;

import lombok.*;

@Data
@NoArgsConstructor
@ToString
@AllArgsConstructor
public class RoleInfoResp {
    private String userFullName;
    private String roleCode;
    private String roleDesc;
    private Boolean isActive;
}
