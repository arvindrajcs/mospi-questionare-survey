package com.mospi.questionare_survey.service;

import com.mospi.questionare_survey.request.*;
import com.mospi.questionare_survey.response.*;

import java.util.List;

public interface MappingService {

    //**********************MAPPING ROLE-MENU******************************/

    ApiResponse<DefaultResponse> createRoleMenuMapping(Long userId, RoleMenuReq request);

    ApiResponse<DefaultResponse> updateRoleMenuStatus(Long userId, RoleMenuStatusReq request);

    ApiResponseWithPageable<List<RoleMenuResp>> getAllRoleMenusWithPage(GetRoleMenuReq request);


    //**********************MAPPING USER-ROLE******************************/

    ApiResponse<DefaultResponse> createUserRoll(Long userId, UserRoleReq request);

    ApiResponse<DefaultResponse> updateUserRollStatus(Long userId, UserRoleStatusReq request);

    ApiResponseWithPageable<List<UserRoleResp>> getAllUserRoll(GetUserRoleReq request);
}
