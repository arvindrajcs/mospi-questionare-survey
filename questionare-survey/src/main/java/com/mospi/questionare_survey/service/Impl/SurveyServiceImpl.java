package com.mospi.questionare_survey.service.Impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.mospi.questionare_survey.entity.*;
import com.mospi.questionare_survey.exception.SDDException;
import com.mospi.questionare_survey.helperUtil.HelperUtils;
import com.mospi.questionare_survey.helperUtil.ResponseUtils;
import com.mospi.questionare_survey.repository.*;
import com.mospi.questionare_survey.request.*;
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


@Service
@RequiredArgsConstructor
@Slf4j
public class SurveyServiceImpl implements SurveyService {

    private final Logger logger = LoggerFactory.getLogger(SurveyServiceImpl.class);

    @Autowired
    private HelperUtils helperUtils;

    @Autowired
    private MasStatusRepo masStatusRepo;

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

    @Autowired
    private MasEnquiryTypeRepo masEnquiryTypeRepo;

    @Autowired
    private MasFrequencyTypeRepo masFrequencyTypeRepo;

    @Autowired
    private MasSeasonalityFactorRepo masSeasonalityFactorRepo;

    @Autowired
    private TemplateDetailsRepo templateDetailsRepo;

    @Autowired
    private MasScheduleAttributesRepo masScheduleAttributesRepo;


    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse<DefaultResponse> createSurveyDetails(Long userId, CreateSurveyReq request) {
        logger.info("Starting survey design creation for user: {}", userId);

        try {
            // 1. Validate and fetch master dependencies
            MasDivision division = masDivisionRepo.findById(request.getDivisionId())
                    .orElseThrow(() -> new SDDException(false, "Division not found for ID: " + request.getDivisionId()));

            MasEnquiryType enquiryType = masEnquiryTypeRepo.findById(request.getEnquiryTypeId())
                    .orElseThrow(() -> new SDDException(false, "Enquiry Type not found for ID: " + request.getEnquiryTypeId()));

            MasFrequencyType frequencyType = masFrequencyTypeRepo.findById(request.getFrequencyTypeId())
                    .orElseThrow(() -> new SDDException(false, "Frequency Type not found for ID: " + request.getFrequencyTypeId()));

            MasSeasonalityFactor seasonalityFactor = masSeasonalityFactorRepo.findById(request.getSeasonalityFactorId())
                    .orElseThrow(() -> new SDDException(false, "Seasonality Factor not found for ID: " + request.getSeasonalityFactorId()));

            // 3. Create and Save SurveyDetails using the builder
            SurveyDetails survey = SurveyDetails.builder()
                    .title(request.getTitle())
                    .periodicCycle(request.getPeriodicCycle())
                    .surveyCode(request.getSurveyCode())
                    .division(division)
                    .enquiryType(enquiryType)
                    .frequencyType(frequencyType)
                    .seasonalityFactor(seasonalityFactor)
                    .surveyDuration(request.getSurveyDuration())
                    .ruralSamplingFrame(request.getRuralSamplingFrame())
                    .urbanSamplingFrame(request.getUrbanSamplingFrame())
                    .samplingSize(request.getSamplingSize())
                    .surveyObjective(request.getSurveyObjective())
                    .remarks(request.getRemarks())
//                    .status(masStatusRepo.findById(1L).orElseThrow(() -> new SDDException(false, "Status not found")))
                    .status(masStatusRepo.getReferenceById(1L))
                    .isActive(true)
                    .createdBy(userId)
                    .build();

            surveyDetailsRepo.save(survey);

            logger.info("Survey design created successfully with code: {}", request.getSurveyCode());

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


    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse<DefaultResponse> createTemplateDetails(Long userId, CreateTemplateReq request) {
        logger.info("Starting template details creation for survey ID: {} by user: {}", request.getSurveyId(), userId);

        try {
            // 1. Validate and fetch parent SurveyDetails entity
            SurveyDetails survey = surveyDetailsRepo.findById(request.getSurveyId())
                    .orElseThrow(() -> new SDDException(false, "Survey not found for ID: " + request.getSurveyId()));

            // 2. Build and save TemplateDetails
            TemplateDetails templateDetails = TemplateDetails.builder()
                    .templateName(request.getTemplateName())
                    .templateType(request.getTemplateType())
                    .survey(survey)
                    .status(masStatusRepo.getReferenceById(1L))
                    .isActive(true)
                    .createdBy(userId)
                    .build();

            templateDetailsRepo.save(templateDetails);

            logger.info("Template details created successfully for survey ID: {}", request.getSurveyId());

            // 3. Prepare success response
            DefaultResponse defaultResponse = new DefaultResponse();
            defaultResponse.setMsg("Template details created successfully");
            return ResponseUtils.createSuccessResponse(defaultResponse, new TypeReference<DefaultResponse>() {});

        } catch (SDDException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error while creating template details", e);
            throw new SDDException(false, "Unexpected error during template details creation");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse<DefaultResponse> createScheduleDetails(Long userId, CreateScheduleReq request) {
        logger.info("Starting schedule details creation for survey ID: {} by user: {}", request.getSurveyId(), userId);

        try {
            // 1. Validate and fetch parent SurveyDetails entity
            SurveyDetails survey = surveyDetailsRepo.findById(request.getSurveyId())
                    .orElseThrow(() -> new SDDException(false, "Survey not found for ID: " + request.getSurveyId()));

            // 2. Validate and fetch MasScheduleType master dependency
            MasScheduleType scheduleType = masScheduleTypeRepo.findById(request.getScheduleTypeId())
                    .orElseThrow(() -> new SDDException(false, "Schedule Type not found for ID: " + request.getScheduleTypeId()));

            // 3. Validate and fetch MasScheduleAttributes master dependency
            MasScheduleAttributes scheduleAttributes = masScheduleAttributesRepo.findById(request.getScheduleAttributesId())
                    .orElseThrow(() -> new SDDException(false, "Schedule Attributes not found for ID: " + request.getScheduleAttributesId()));

            // 4. Build and save ScheduleDetails
            ScheduleDetails scheduleDetails = ScheduleDetails.builder()
                    .survey(survey)
                    .scheduleType(scheduleType)
                    .scheduleAttributes(scheduleAttributes)
                    .status(masStatusRepo.getReferenceById(1L))
                    .isActive(true)
                    .createdBy(userId)
                    .build();

            scheduleDetailsRepo.save(scheduleDetails);

            logger.info("Schedule details created successfully for survey ID: {}", request.getSurveyId());

            // 5. Prepare success response
            DefaultResponse defaultResponse = new DefaultResponse();
            defaultResponse.setMsg("Schedule details created successfully");
            return ResponseUtils.createSuccessResponse(defaultResponse, new TypeReference<DefaultResponse>() {});

        } catch (SDDException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error while creating schedule details", e);
            throw new SDDException(false, "Unexpected error during schedule details creation");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse<DefaultResponse> createBlockDetails(Long userId, BlockDetailsReq request) {
        logger.info("Starting block details creation for schedule ID: {} and survey ID: {} by user: {}",
                request.getScheduleId(), request.getSurveyId(), userId);

        try {
            // 1. Validate and fetch parent SurveyDetails entity
            SurveyDetails survey = surveyDetailsRepo.findById(request.getSurveyId())
                    .orElseThrow(() -> new SDDException(false, "Survey not found for ID: " + request.getSurveyId()));

            // 2. Validate and fetch parent ScheduleDetails entity
            ScheduleDetails schedule = scheduleDetailsRepo.findById(request.getScheduleId())
                    .orElseThrow(() -> new SDDException(false, "Schedule not found for ID: " + request.getScheduleId()));

            // 3. Build and save BlockDetails
            BlockDetails blockDetails = BlockDetails.builder()
                    .survey(survey)
                    .schedule(schedule)
                    .blockHeading(request.getBlockHeading())
                    .blockCode(request.getBlockCode())
                    .status(masStatusRepo.getReferenceById(1L))
                    .isActive(true)
                    .createdBy(userId)
                    .build();

            blockDetailsRepo.save(blockDetails);

            logger.info("Block details created successfully with code: {}", request.getBlockCode());

            // 4. Prepare success response
            DefaultResponse defaultResponse = new DefaultResponse();
            defaultResponse.setMsg("Block details created successfully");
            return ResponseUtils.createSuccessResponse(defaultResponse, new TypeReference<DefaultResponse>() {});

        } catch (SDDException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error while creating block details", e);
            throw new SDDException(false, "Unexpected error during block details creation");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse<DefaultResponse> createQuestionDetails(Long userId, QuestinDetailsReq request) {
        logger.info("Starting question details creation for block ID: {} and survey ID: {} by user: {}",
                request.getBlockId(), request.getSurveyId(), userId);

        try {
            // 1. Validate and fetch parent SurveyDetails entity
            SurveyDetails survey = surveyDetailsRepo.findById(request.getSurveyId())
                    .orElseThrow(() -> new SDDException(false, "Survey not found for ID: " + request.getSurveyId()));

            // 2. Validate and fetch parent BlockDetails entity
            BlockDetails block = blockDetailsRepo.findById(request.getBlockId())
                    .orElseThrow(() -> new SDDException(false, "Block not found for ID: " + request.getBlockId()));

            // 3. Build and save QuestionDetails
            QuestionDetails questionDetails = QuestionDetails.builder()
                    .survey(survey)
                    .block(block)
                    .questionNumber(request.getQuestionNumber())
                    .fieldInstructions(request.getFieldInstructions())
                    .questionHeading(request.getQuestionHeading())
                    .displayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 1)
                    .isMandatory(request.isMandatory())
                    .status(masStatusRepo.getReferenceById(1L))
                    .isActive(true)
                    .createdBy(userId)
                    .build();

            questionDetailsRepo.save(questionDetails);

            logger.info("Question details created successfully with number: {}", request.getQuestionNumber());

            // 4. Prepare success response
            DefaultResponse defaultResponse = new DefaultResponse();
            defaultResponse.setMsg("Question details created successfully");
            return ResponseUtils.createSuccessResponse(defaultResponse, new TypeReference<DefaultResponse>() {});

        } catch (SDDException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error while creating question details", e);
            throw new SDDException(false, "Unexpected error during question details creation");
        }
    }


}
