package com.mospi.questionare_survey.controller;


import com.mospi.questionare_survey.entity.MasMenu;
import com.mospi.questionare_survey.request.CreateMenuReq;
import com.mospi.questionare_survey.request.GetMenuReq;
import com.mospi.questionare_survey.request.MenuStatusReq;
import com.mospi.questionare_survey.response.ApiResponse;
import com.mospi.questionare_survey.response.ApiResponseWithPageable;
import com.mospi.questionare_survey.response.DefaultResponse;
import com.mospi.questionare_survey.service.MasterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/mas")
@CrossOrigin(origins = "*")
@Tag(name = "masterController",description = "This controller only for Master Table Data.")
@Slf4j
public class MasterController {

    private final Logger logger = LoggerFactory.getLogger(MasterController.class);

    @Autowired
    private MasterService masterService;


    @Operation(summary = "This API is used to create Menu with basic Details")
    @PostMapping("/menu/create-menu")
    public ResponseEntity<ApiResponse<DefaultResponse>> createMenu(@RequestHeader("userId") Long userId, @RequestBody CreateMenuReq request) {
        logger.info("trying to call createMenu API");
        return new ResponseEntity<>(masterService.createMenu(userId, request), HttpStatus.OK);
    }

    @Operation(summary = "This API is used to update Menu with basic Details")
    @PostMapping("/menu/update-menu")
    public ResponseEntity<ApiResponse<DefaultResponse>> updateMenu(@RequestHeader("userId") Long userId, @RequestBody CreateMenuReq request) {
        logger.info("trying to call updateMenu API");
        return new ResponseEntity<>(masterService.updateMenu(userId, request), HttpStatus.OK);
    }

    @Operation(summary = "This API is used to update Menu status Active or InActive")
    @PostMapping("/menu/update-status")
    public ResponseEntity<ApiResponse<DefaultResponse>> updateMenuStatus(@RequestHeader("userId") Long userId, @RequestBody MenuStatusReq request) {
        logger.info("trying to call updateMenuStatus API");
        return new ResponseEntity<>(masterService.updateMenuStatus(userId, request), HttpStatus.OK);
    }

    @Operation(summary = "This API is used to Get Menu using MenuId")
    @PostMapping("/menu/get-menu")
    public ResponseEntity<ApiResponse<MasMenu>> getMenuByMenuId(@RequestBody GetMenuReq request) {
        logger.info("trying to call getMenuByMenuId API");
        return new ResponseEntity<>(masterService.getMenuByMenuId(request), HttpStatus.OK);
    }

    @Operation(summary = "This API is used to Get All Menu List")
    @PostMapping("/menu/all-menu")
    public ResponseEntity<ApiResponseWithPageable<List<MasMenu>>> getAllMenus(@RequestBody GetMenuReq request) {
        logger.info("Trying to call getAllMenus");
        return new ResponseEntity<>(masterService.getAllMenus(request), HttpStatus.OK);
    }
}
