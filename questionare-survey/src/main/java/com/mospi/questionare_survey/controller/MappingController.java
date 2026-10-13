package com.mospi.questionare_survey.controller;


import com.mospi.questionare_survey.request.*;
import com.mospi.questionare_survey.response.*;
import com.mospi.questionare_survey.service.MappingService;
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
@RequestMapping("/api/v1/mapping")
@CrossOrigin(origins = "*")
@Tag(name = "mappingController",description = "This controller only for Mapping Related task Like Role-Menu or User-Role Mapping.")
@Slf4j
public class MappingController {

    private final Logger logger = LoggerFactory.getLogger(MappingController.class);

    @Autowired
    private MappingService mappingService;



    //**********************MAPPING ROLE-MENU******************************/

    @Operation(summary = "This API is used to create Role Menu Mapping. One RoleId Mapped with one or many MenuId")
    @PostMapping("/role-menu/create")
    public ResponseEntity<ApiResponse<DefaultResponse>> createRoleMenuMapping(@RequestHeader("userId") Long userId, @RequestBody RoleMenuReq request) {
        logger.info("trying to call createRoleMenuMapping API");
        return new ResponseEntity<>(mappingService.createRoleMenuMapping(userId, request), HttpStatus.OK);
    }

    @Operation(summary = "This API is used to update Role Menu Mapping status Active or InActive")
    @PostMapping("/role-menu/update-status")
    public ResponseEntity<ApiResponse<DefaultResponse>> updateRoleMenuStatus(@RequestHeader("userId") Long userId, @RequestBody RoleMenuStatusReq request) {
        logger.info("trying to call updateRoleMenuStatus API");
        return new ResponseEntity<>(mappingService.updateRoleMenuStatus(userId, request), HttpStatus.OK);
    }

    @Operation(summary = "This API is used to Get All Role-Menu Mapping List or By RollId")
    @PostMapping("/role-menu/get-all")
    public ResponseEntity<ApiResponseWithPageable<List<RoleMenuResp>>> getAllRoleMenusWithPage(@RequestBody GetRoleMenuReq request) {
        logger.info("Trying to call getAllRoleMenusWithPage");
        return new ResponseEntity<>(mappingService.getAllRoleMenusWithPage(request), HttpStatus.OK
        );
    }

    //**********************MAPPING USER-ROLE******************************/

    @Operation(summary = "This API is used to create User-Role Mapping. One userId Mapped with one or many RollId")
    @PostMapping("/user-role/create")
    public ResponseEntity<ApiResponse<DefaultResponse>> createUserRoll(@RequestHeader("userId") Long userId, @RequestBody UserRoleReq request) {
        logger.info("trying to call createUserRoll API");
        return new ResponseEntity<>(mappingService.createUserRoll(userId, request), HttpStatus.OK);
    }

    @Operation(summary = "This API is used to update User-Role Mapping status Active or InActive")
    @PostMapping("/user-role/update-status")
    public ResponseEntity<ApiResponse<DefaultResponse>> updateUserRollStatus(@RequestHeader("userId") Long userId, @RequestBody UserRoleStatusReq request) {
        logger.info("trying to call updateUserRollStatus API");
        return new ResponseEntity<>(mappingService.updateUserRollStatus(userId, request), HttpStatus.OK);
    }

    @Operation(summary = "This API is used to Get All User-Role Mapping List or By UserId")
    @PostMapping("/user-role/get-all")
    public ResponseEntity<ApiResponseWithPageable<List<UserRoleResp>>> getAllUserRoll(@RequestBody GetUserRoleReq request) {
        logger.info("Trying to call getAllUserRoll");
        return new ResponseEntity<>(mappingService.getAllUserRoll(request), HttpStatus.OK
        );
    }

}
