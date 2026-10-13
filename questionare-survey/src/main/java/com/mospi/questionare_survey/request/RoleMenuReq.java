package com.mospi.questionare_survey.request;

import lombok.*;

import java.util.ArrayList;

@Data
@NoArgsConstructor
@ToString
@AllArgsConstructor
@Builder
public class RoleMenuReq {

    private Long roleId;
    private ArrayList<Long>menuId;
}
