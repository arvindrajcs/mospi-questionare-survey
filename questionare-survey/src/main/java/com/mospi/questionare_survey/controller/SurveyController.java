package com.mospi.questionare_survey.controller;

import com.mospi.questionare_survey.request.CreateSurveyReq;
import com.mospi.questionare_survey.request.CreateUserReq;
import com.mospi.questionare_survey.response.ApiResponse;
import com.mospi.questionare_survey.response.DefaultResponse;
import com.mospi.questionare_survey.service.SurveyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/survey")
@CrossOrigin(origins = "*")
@Tag(name = "SurveyController",description = "This controller only for Survey Design Details Related task.")
@Slf4j
public class SurveyController {
    private final Logger logger = LoggerFactory.getLogger(SurveyController.class);

    @Autowired
    private SurveyService surveyService;

    @Operation(summary = "This API is used to create User with basic Details")
    @PostMapping("/user/create-user")
    public ResponseEntity<ApiResponse<DefaultResponse>> createSurveyDesign(@RequestHeader("userId") Long userId, @RequestBody CreateSurveyReq request) {
        logger.info("trying to call createSurveyDesign API");
        return new ResponseEntity<>(surveyService.createSurveyDesign(userId, request), HttpStatus.OK);
    }
}
