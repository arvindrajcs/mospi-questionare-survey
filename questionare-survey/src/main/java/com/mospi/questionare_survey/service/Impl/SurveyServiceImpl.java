package com.mospi.questionare_survey.service.Impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.mospi.questionare_survey.entity.*;
import com.mospi.questionare_survey.exception.SDDException;
import com.mospi.questionare_survey.helperUtil.HelperUtils;
import com.mospi.questionare_survey.helperUtil.ResponseUtils;
import com.mospi.questionare_survey.repository.*;
import com.mospi.questionare_survey.request.BlockDetailsReq;
import com.mospi.questionare_survey.request.CreateSurveyReq;
import com.mospi.questionare_survey.request.QuestinDetailsReq;
import com.mospi.questionare_survey.response.ApiResponse;
import com.mospi.questionare_survey.response.DefaultResponse;
import com.mospi.questionare_survey.service.SurveyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class SurveyServiceImpl implements SurveyService {

    private final Logger logger = LoggerFactory.getLogger(SurveyServiceImpl.class);

    @Autowired
    private HelperUtils helperUtill;

    @Autowired
    private MasSurveyTypeRepo masSurveyTypeRepo;

    @Autowired
    private MasDivisionRepo masDivisionRepo;

    @Autowired
    private MasScheduleTypeRepo masScheduleTypeRepo;

    @Autowired
    private SurveyDetailsRepo surveyDetailsRepo;

    @Autowired
    private ScheduleDetailsRepo scheduleDetailsRepo;

    @Autowired
    private BlockDetailsRepo blockDetailsRepo;

    @Autowired
    private QuestionDetailsRepo questionDetailsRepo;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse<DefaultResponse> createSurveyDesign(Long userId, CreateSurveyReq request) {
        logger.info("Starting survey design creation for user: {}", userId);

        try {
            // 1. Validate and fetch master dependencies
            MasSurveyType surveyType = masSurveyTypeRepo.findById(request.getSurveyTypeId())
                    .orElseThrow(() -> new SDDException(false, "Survey Type not found for ID: " + request.getSurveyTypeId()));

            MasDivision division = masDivisionRepo.findById(request.getDivisionId())
                    .orElseThrow(() -> new SDDException(false, "Division not found for ID: " + request.getDivisionId()));

            MasScheduleType scheduleType = masScheduleTypeRepo.findById(request.getScheduleTypeId())
                    .orElseThrow(() -> new SDDException(false, "Schedule Type not found for ID: " + request.getScheduleTypeId()));

            // 2. Generate Survey Code (e.g., TITLE-2026)
            String currentYear = String.valueOf(LocalDate.now().getYear());
            String sanitizedTitle = request.getTitle().trim().replaceAll("\\s+", "-").toUpperCase();
            String surveyCode = sanitizedTitle + "-" + currentYear;

            // Optional: Check if survey code already exists to avoid unique constraint violations
            if (surveyDetailsRepo.existsBySurveyCode(surveyCode)) {
                surveyCode = surveyCode + "-" + System.currentTimeMillis(); // ensure uniqueness if duplicate title exists
            }

            // 3. Create and Save SurveyDetails
            SurveyDetails survey = new SurveyDetails();
            survey.setTitle(request.getTitle());
            survey.setSurveyType(surveyType);
            survey.setSurveyCode(surveyCode);
            survey.setDivision(division);
            survey.setDescription(request.getDescription());
            survey.setTemplateName(request.getTemplateName());
            survey.setActive(true);
            survey.setCreatedBy(userId);

            survey = surveyDetailsRepo.save(survey);

            // 4. Create and Save ScheduleDetails
            ScheduleDetails schedule = new ScheduleDetails();
            schedule.setSurvey(survey);
            schedule.setScheduleType(scheduleType);
            schedule.setActive(true);
            schedule.setCreatedBy(userId);

            schedule = scheduleDetailsRepo.save(schedule);

            // 5. Create and Save BlockDetails and their QuestionDetails
            if (request.getBlockDetailsList() != null && !request.getBlockDetailsList().isEmpty()) {
                for (BlockDetailsReq blockReq : request.getBlockDetailsList()) {
                    BlockDetails block = new BlockDetails();
                    block.setSchedule(schedule);
                    block.setBlockHeading(blockReq.getBlockHeading());
                    block.setDisplayOrder(blockReq.getDisplayOrder() != null ? blockReq.getDisplayOrder() : 1);
                    block.setFieldsData(blockReq.getFieldsData());
                    block.setActive(true);
                    block.setCreatedBy(userId);

                    block = blockDetailsRepo.save(block);

                    // Save Questions under this Block
                    if (blockReq.getQuestionDetailsList() != null && !blockReq.getQuestionDetailsList().isEmpty()) {
                        for (QuestinDetailsReq qReq : blockReq.getQuestionDetailsList()) {
                            QuestionDetails question = new QuestionDetails();
                            question.setBlock(block);
                            question.setQuestionHeading(qReq.getQuestionHeading());
                            question.setDisplayOrder(qReq.getDisplayOrder() != null ? qReq.getDisplayOrder() : 1);
                            question.setFieldsData(qReq.getFieldsData());
                            question.setActive(true);
                            question.setCreatedBy(userId);

                            questionDetailsRepo.save(question);
                        }
                    }
                }
            }

            logger.info("Survey design created successfully with code: {}", surveyCode);

            DefaultResponse defaultResponse = new DefaultResponse();
            defaultResponse.setMsg("Survey design created successfully");
            return ResponseUtils.createSuccessResponse(defaultResponse, new TypeReference<DefaultResponse>() {});

        } catch (SDDException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error while creating survey design", e);
            throw new SDDException(false, "Unexpected error during survey design creation");
        }
    }



}
