package com.mospi.questionare_survey.controller;

import com.mospi.questionare_survey.request.*;
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

    @Operation(summary = "This API is used to capture basic survey details")
    @PostMapping("/save/survey-details")
    public ResponseEntity<ApiResponse<DefaultResponse>> createSurveyDetails(@RequestHeader("userId") Long userId, @RequestBody CreateSurveyReq request) {
        logger.info("trying to call createSurveyDetails API");
        return new ResponseEntity<>(surveyService.createSurveyDetails(userId, request), HttpStatus.OK);
    }


    @Operation(summary = "This API is used to capture survey template details")
    @PostMapping("/save/template-details")
    public ResponseEntity<ApiResponse<DefaultResponse>> createTemplateDetails(@RequestHeader("userId") Long userId, @RequestBody CreateTemplateReq request) {
        logger.info("trying to call createTemplateDetails API");
        return new ResponseEntity<>(surveyService.createTemplateDetails(userId, request), HttpStatus.OK);
    }

    @Operation(summary = "This API is used to capture schedule details")
    @PostMapping("/save/schedule-details")
    public ResponseEntity<ApiResponse<DefaultResponse>> createScheduleDetails(@RequestHeader("userId") Long userId, @RequestBody CreateScheduleReq request) {
        logger.info("trying to call createScheduleDetails API");
        return new ResponseEntity<>(surveyService.createScheduleDetails(userId, request), HttpStatus.OK);
    }

    @Operation(summary = "This API is used to capture block details")
    @PostMapping("/save/block-details")
    public ResponseEntity<ApiResponse<DefaultResponse>> createBlockDetails(@RequestHeader("userId") Long userId, @RequestBody BlockDetailsReq request) {
        logger.info("trying to call createBlockDetails API");
        return new ResponseEntity<>(surveyService.createBlockDetails(userId, request), HttpStatus.OK);
    }

    @Operation(summary = "This API is used to capture Question details")
    @PostMapping("/save/question-details")
    public ResponseEntity<ApiResponse<DefaultResponse>> createQuestionDetails(@RequestHeader("userId") Long userId, @RequestBody QuestinDetailsReq request) {
        logger.info("trying to call createQuestionDetails API");
        return new ResponseEntity<>(surveyService.createQuestionDetails(userId, request), HttpStatus.OK);
    }



}
