package com.mospi.questionare_survey.request;

import lombok.*;

@Data
@NoArgsConstructor
@ToString
@AllArgsConstructor
@Builder
public class RoleMenuStatusReq {

    private Long roleMenuMappingId;
    private boolean isActive;
}
