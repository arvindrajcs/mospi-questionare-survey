package com.mospi.questionare_survey.request;

import lombok.*;

@Data
@NoArgsConstructor
@ToString
@AllArgsConstructor
@Builder
public class UserRoleStatusReq {

    private Long userRoleMappingId;
    private boolean isActive;
}
