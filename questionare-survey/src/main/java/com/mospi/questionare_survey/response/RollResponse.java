package com.mospi.questionare_survey.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString
@AllArgsConstructor
public class RollResponse {

    private Long roleId;
    private String roleName;
    private String roleDescription;
}
