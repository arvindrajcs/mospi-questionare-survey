package com.mospi.questionare_survey.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@NoArgsConstructor
@ToString
@AllArgsConstructor
public class RoleMenuResp {
    private Long roleId;
    private String roleName;
    private List<MenuResponse> menuList;
}
