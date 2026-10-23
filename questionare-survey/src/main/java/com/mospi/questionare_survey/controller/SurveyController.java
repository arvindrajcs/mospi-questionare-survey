package com.mospi.questionare_survey.controller;

import com.mospi.questionare_survey.entity.*;
import com.mospi.questionare_survey.request.*;
import com.mospi.questionare_survey.response.*;
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

import java.util.List;

@RestController
@RequestMapping("/api/v1/survey")
@CrossOrigin(origins = "*")
@Tag(name = "SurveyController",description = "This controller only for Survey Design Details Related task.")
@Slf4j
public class SurveyController {
    private final Logger logger = LoggerFactory.getLogger(SurveyController.class);

    @Autowired
    private SurveyService surveyService;


    //**********************SURVEY DETAILS******************************/


    @Operation(summary = "This API is used to capture basic survey details")
    @PostMapping("/save/survey-details")
    public ResponseEntity<ApiResponse<DefaultResponse>> createSurveyDetails(@RequestHeader("userId") Long userId, @RequestBody CreateSurveyReq request) {
        logger.info("trying to call createSurveyDetails API");
        return new ResponseEntity<>(surveyService.createSurveyDetails(userId, request), HttpStatus.OK);
    }

    @Operation(summary = "This API is used to update basic survey details")
    @PostMapping("/update/survey-details")
    public ResponseEntity<ApiResponse<DefaultResponse>> updateSurveyDetails(
            @RequestHeader("userId") Long userId,
            @RequestBody UpdateSurveyReq request) {
        logger.info("trying to call updateSurveyDetails API for ID: {}", request.getSurveyId());
        return new ResponseEntity<>(surveyService.updateSurveyDetails(userId, request), HttpStatus.OK);
    }

    @Operation(summary = "This API is used to update survey status (active/inactive)")
    @PostMapping("/update/survey-status")
    public ResponseEntity<ApiResponse<DefaultResponse>> updateSurveyStatus(
            @RequestHeader("userId") Long userId,
            @RequestBody SurveyStatusReq request) {
        logger.info("trying to call updateSurveyStatus API for ID: {}", request.getSurveyId());
        return new ResponseEntity<>(surveyService.updateSurveyStatus(userId, request), HttpStatus.OK);
    }

    @Operation(summary = "This API is used to get survey details by ID")
    @PostMapping("/get/survey-details")
    public ResponseEntity<ApiResponseWithPageable<List<SurveyResp>>> getSurveyDetailsById(@RequestBody GetSurveyReq request) {
        logger.info("trying to call getSurveyDetailsById API for ID: {}", request.getSurveyId());
        return new ResponseEntity<>(surveyService.getSurveyDetailsById(request), HttpStatus.OK);
    }

    @Operation(summary = "This API is used to get all survey details with pagination")
    @PostMapping("/get/all-survey-details")
    public ResponseEntity<ApiResponseWithPageable<List<SurveyResp>>> getAllSurveyDetails(@RequestBody GetSurveyReq request) {
        logger.info("trying to call getAllSurveyDetails API");
        return new ResponseEntity<>(surveyService.getAllSurveyDetails(request), HttpStatus.OK);
    }


    //**********************TEMPLATE DETAILS******************************/



    @Operation(summary = "This API is used to capture survey template details")
    @PostMapping("/save/template-details")
    public ResponseEntity<ApiResponse<DefaultResponse>> createTemplateDetails(@RequestHeader("userId") Long userId, @RequestBody CreateTemplateReq request) {
        logger.info("trying to call createTemplateDetails API");
        return new ResponseEntity<>(surveyService.createTemplateDetails(userId, request), HttpStatus.OK);
    }

    @Operation(summary = "This API is used to update survey template details")
    @PostMapping("/update/template-details")
    public ResponseEntity<ApiResponse<DefaultResponse>> updateTemplateDetails(@RequestHeader("userId") Long userId, @RequestBody UpdateTemplateReq request) {
        logger.info("trying to call updateTemplateDetails API for ID: {}", request.getTemplateId());
        return new ResponseEntity<>(surveyService.updateTemplateDetails(userId, request), HttpStatus.OK);
    }

    @Operation(summary = "This API is used to update template status (active/inactive)")
    @PostMapping("/update/template-status")
    public ResponseEntity<ApiResponse<DefaultResponse>> updateTemplateStatus(@RequestHeader("userId") Long userId, @RequestBody TemplateStatusReq request) {
        logger.info("trying to call updateTemplateStatus API for ID: {}", request.getTemplateId());
        return new ResponseEntity<>(surveyService.updateTemplateStatus(userId, request), HttpStatus.OK);
    }

    @Operation(summary = "This API is used to get template details by ID")
    @PostMapping("/get/template-details")
    public ResponseEntity<ApiResponseWithPageable<List<TemplateResp>>>  getTemplateDetailsById(@RequestBody GetTemplateReq request) {
        logger.info("trying to call getTemplateDetailsById API for ID: {}", request.getTemplateId());
        return new ResponseEntity<>(surveyService.getTemplateDetailsById(request), HttpStatus.OK);
    }

    @Operation(summary = "This API is used to get all template details with pagination")
    @PostMapping("/get/all-template-details")
    public ResponseEntity<ApiResponseWithPageable<List<TemplateResp>>> getAllTemplateDetails(@RequestBody GetTemplateReq request) {
        logger.info("trying to call getAllTemplateDetails API");
        return new ResponseEntity<>(surveyService.getAllTemplateDetails(request), HttpStatus.OK);
    }


    //**********************SCHEDULE DETAILS******************************/



    @Operation(summary = "This API is used to capture schedule details")
    @PostMapping("/save/schedule-details")
    public ResponseEntity<ApiResponse<DefaultResponse>> createScheduleDetails(@RequestHeader("userId") Long userId, @RequestBody CreateScheduleReq request) {
        logger.info("trying to call createScheduleDetails API");
        return new ResponseEntity<>(surveyService.createScheduleDetails(userId, request), HttpStatus.OK);
    }

    @Operation(summary = "This API is used to update survey schedule details")
    @PostMapping("/update/schedule-details")
    public ResponseEntity<ApiResponse<DefaultResponse>> updateScheduleDetails(@RequestHeader("userId") Long userId, @RequestBody UpdateScheduleReq request) {
        logger.info("trying to call updateScheduleDetails API for ID: {}", request.getScheduleId());
        return new ResponseEntity<>(surveyService.updateScheduleDetails(userId, request), HttpStatus.OK);
    }

    @Operation(summary = "This API is used to update schedule status (active/inactive)")
    @PostMapping("/update/schedule-status")
    public ResponseEntity<ApiResponse<DefaultResponse>> updateScheduleStatus(@RequestHeader("userId") Long userId, @RequestBody ScheduleStatusReq request) {
        logger.info("trying to call updateScheduleStatus API for ID: {}", request.getScheduleId());
        return new ResponseEntity<>(surveyService.updateScheduleStatus(userId, request), HttpStatus.OK);
    }

    @Operation(summary = "This API is used to get schedule details by schedule ID or User ID with pagination")
    @PostMapping("/get/schedule-details")
    public ResponseEntity<ApiResponseWithPageable<List<ScheduleResp>>> getScheduleDetails(@RequestBody GetScheduleReq request) {
        logger.info("trying to call getScheduleDetails API");
        return new ResponseEntity<>(surveyService.getScheduleDetailsByIdOrUser(request), HttpStatus.OK);
    }

    @Operation(summary = "This API is used to get all schedule details with pagination")
    @PostMapping("/get/all-schedule-details")
    public ResponseEntity<ApiResponseWithPageable<List<ScheduleResp>>> getAllScheduleDetails(@RequestBody GetScheduleReq request) {
        logger.info("trying to call getAllScheduleDetails API");
        return new ResponseEntity<>(surveyService.getAllScheduleDetails(request), HttpStatus.OK);
    }

    //**********************BLOCK DETAILS******************************/


    @Operation(summary = "This API is used to capture block details")
    @PostMapping("/save/block-details")
    public ResponseEntity<ApiResponse<DefaultResponse>> createBlockDetails(@RequestHeader("userId") Long userId, @RequestBody BlockDetailsReq request) {
        logger.info("trying to call createBlockDetails API");
        return new ResponseEntity<>(surveyService.createBlockDetails(userId, request), HttpStatus.OK);
    }

    @Operation(summary = "This API is used to update survey block details")
    @PostMapping("/update/block-details")
    public ResponseEntity<ApiResponse<DefaultResponse>> updateBlockDetails(@RequestHeader("userId") Long userId, @RequestBody UpdateBlockReq request) {
        logger.info("trying to call updateBlockDetails API for ID: {}", request.getBlockId());
        return new ResponseEntity<>(surveyService.updateBlockDetails(userId, request), HttpStatus.OK);
    }

    @Operation(summary = "This API is used to update block status (active/inactive)")
    @PostMapping("/update/block-status")
    public ResponseEntity<ApiResponse<DefaultResponse>> updateBlockStatus(@RequestHeader("userId") Long userId, @RequestBody BlockStatusReq request) {
        logger.info("trying to call updateBlockStatus API for ID: {}", request.getBlockId());
        return new ResponseEntity<>(surveyService.updateBlockStatus(userId, request), HttpStatus.OK);
    }

    @Operation(summary = "This API is used to get block details by block ID or User ID with pagination")
    @PostMapping("/get/block-details")
    public ResponseEntity<ApiResponseWithPageable<List<BlockResp>>> getBlockDetails(@RequestBody GetBlockReq request) {
        logger.info("trying to call getBlockDetails API");
        return new ResponseEntity<>(surveyService.getBlockDetailsByIdOrUser(request), HttpStatus.OK);
    }

    @Operation(summary = "This API is used to get all block details with pagination")
    @PostMapping("/get/all-block-details")
    public ResponseEntity<ApiResponseWithPageable<List<BlockResp>>> getAllBlockDetails(@RequestBody GetBlockReq request) {
        logger.info("trying to call getAllBlockDetails API");
        return new ResponseEntity<>(surveyService.getAllBlockDetails(request), HttpStatus.OK);
    }


    //**********************QUESTION DETAILS******************************/


    @Operation(summary = "This API is used to capture Question details")
    @PostMapping("/save/question-details")
    public ResponseEntity<ApiResponse<DefaultResponse>> createQuestionDetails(@RequestHeader("userId") Long userId, @RequestBody QuestinDetailsReq request) {
        logger.info("trying to call createQuestionDetails API");
        return new ResponseEntity<>(surveyService.createQuestionDetails(userId, request), HttpStatus.OK);
    }

    @Operation(summary = "This API is used to update survey question details")
    @PostMapping("/update/question-details")
    public ResponseEntity<ApiResponse<DefaultResponse>> updateQuestionDetails(@RequestHeader("userId") Long userId, @RequestBody UpdateQuestionReq request) {
        logger.info("trying to call updateQuestionDetails API for ID: {}", request.getQuestionId());
        return new ResponseEntity<>(surveyService.updateQuestionDetails(userId, request), HttpStatus.OK);
    }

    @Operation(summary = "This API is used to update question status (active/inactive)")
    @PostMapping("/update/question-status")
    public ResponseEntity<ApiResponse<DefaultResponse>> updateQuestionStatus(@RequestHeader("userId") Long userId, @RequestBody QuestionStatusReq request) {
        logger.info("trying to call updateQuestionStatus API for ID: {}", request.getQuestionId());
        return new ResponseEntity<>(surveyService.updateQuestionStatus(userId, request), HttpStatus.OK);
    }

    @Operation(summary = "This API is used to get question details by question ID or User ID with pagination")
    @PostMapping("/get/question-details")
    public ResponseEntity<ApiResponseWithPageable<List<QuestionResp>>> getQuestionDetails(@RequestBody GetQuestionReq request) {
        logger.info("trying to call getQuestionDetails API");
        return new ResponseEntity<>(surveyService.getQuestionDetailsByIdOrUser(request), HttpStatus.OK);
    }

    @Operation(summary = "This API is used to get all question details with pagination")
    @PostMapping("/get/all-question-details")
    public ResponseEntity<ApiResponseWithPageable<List<QuestionResp>>> getAllQuestionDetails(@RequestBody GetQuestionReq request) {
        logger.info("trying to call getAllQuestionDetails API");
        return new ResponseEntity<>(surveyService.getAllQuestionDetails(request), HttpStatus.OK);
    }

    //**********************TEST SURVEY******************************/

    @Operation(summary = "This API is used to get all survey details for Test Survey  by survey ID")
    @PostMapping("/get/test-survey")
    public ResponseEntity<ApiResponse<TestSurveyResp>> getTestSurveyDetails(@RequestBody TestSurveyReq request) {
        logger.info("trying to call getTestSurveyDetails API");
        return new ResponseEntity<>(surveyService.getTestSurveyDetails(request), HttpStatus.OK);
    }

}
