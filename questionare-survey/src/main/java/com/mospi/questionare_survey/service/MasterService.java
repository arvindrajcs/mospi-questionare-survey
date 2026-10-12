package com.mospi.questionare_survey.service;

import com.mospi.questionare_survey.entity.MasMenu;
import com.mospi.questionare_survey.request.CreateMenuReq;
import com.mospi.questionare_survey.request.GetMenuReq;
import com.mospi.questionare_survey.request.MenuStatusReq;
import com.mospi.questionare_survey.response.ApiResponse;
import com.mospi.questionare_survey.response.ApiResponseWithPageable;
import com.mospi.questionare_survey.response.DefaultResponse;

import java.util.List;

public interface MasterService {
    ApiResponse<DefaultResponse> createMenu(Long userId, CreateMenuReq request);

    ApiResponse<DefaultResponse> updateMenu(Long userId, CreateMenuReq request);

    ApiResponse<DefaultResponse> updateMenuStatus(Long userId, MenuStatusReq request);

    ApiResponse<MasMenu> getMenuByMenuId(GetMenuReq request);

    ApiResponseWithPageable<List<MasMenu>> getAllMenus(GetMenuReq request);
}
