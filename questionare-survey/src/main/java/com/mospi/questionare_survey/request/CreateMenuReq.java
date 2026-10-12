package com.mospi.questionare_survey.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = false)
public class CreateMenuReq {
    private Long menuId; // for update
    private String parentId;
    private String childId;
    private String menuUrl;
    private String iconName;
    private String labelName;
    private String menuType;
    private Integer menuOrder;
}
