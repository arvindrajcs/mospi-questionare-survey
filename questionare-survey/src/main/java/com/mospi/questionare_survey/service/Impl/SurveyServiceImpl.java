package com.mospi.questionare_survey.service.Impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.mospi.questionare_survey.entity.*;
import com.mospi.questionare_survey.exception.SDDException;
import com.mospi.questionare_survey.helperUtil.HelperUtils;
import com.mospi.questionare_survey.helperUtil.ResponseUtils;
import com.mospi.questionare_survey.repository.*;
import com.mospi.questionare_survey.request.*;
import com.mospi.questionare_survey.response.*;
import com.mospi.questionare_survey.service.SurveyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


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

    @Autowired
    private QuestionValidationRepo questionValidationRepo;

    @Autowired
    private SkipConditionRepo skipConditionRepo;



    //**********************SURVEY DETAILS******************************/

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
    public ApiResponse<DefaultResponse> updateSurveyDetails(Long userId, UpdateSurveyReq request) {
        logger.info("Starting survey details update for ID: {} by user: {}", request.getSurveyId(), userId);

        try {
            // 1. Fetch existing survey
            SurveyDetails survey = surveyDetailsRepo.findById(request.getSurveyId())
                    .orElseThrow(() -> new SDDException(false, "Survey not found for ID: " + request.getSurveyId()));

            // 2. Validate and fetch master dependencies
            MasDivision division = masDivisionRepo.findById(request.getDivisionId())
                    .orElseThrow(() -> new SDDException(false, "Division not found for ID: " + request.getDivisionId()));

            MasEnquiryType enquiryType = masEnquiryTypeRepo.findById(request.getEnquiryTypeId())
                    .orElseThrow(() -> new SDDException(false, "Enquiry Type not found for ID: " + request.getEnquiryTypeId()));

            MasFrequencyType frequencyType = masFrequencyTypeRepo.findById(request.getFrequencyTypeId())
                    .orElseThrow(() -> new SDDException(false, "Frequency Type not found for ID: " + request.getFrequencyTypeId()));

            MasSeasonalityFactor seasonalityFactor = masSeasonalityFactorRepo.findById(request.getSeasonalityFactorId())
                    .orElseThrow(() -> new SDDException(false, "Seasonality Factor not found for ID: " + request.getSeasonalityFactorId()));

            // 3. Update fields
            survey.setTitle(request.getTitle());
            survey.setPeriodicCycle(request.getPeriodicCycle());
            survey.setSurveyCode(request.getSurveyCode());
            survey.setDivision(division);
            survey.setEnquiryType(enquiryType);
            survey.setFrequencyType(frequencyType);
            survey.setSeasonalityFactor(seasonalityFactor);
            survey.setSurveyDuration(request.getSurveyDuration());
            survey.setRuralSamplingFrame(request.getRuralSamplingFrame());
            survey.setUrbanSamplingFrame(request.getUrbanSamplingFrame());
            survey.setSamplingSize(request.getSamplingSize());
            survey.setSurveyObjective(request.getSurveyObjective());
            survey.setRemarks(request.getRemarks());
            survey.setUpdatedBy(userId);

            surveyDetailsRepo.save(survey);

            logger.info("Survey details updated successfully for ID: {}", request.getSurveyId());

            DefaultResponse defaultResponse = new DefaultResponse();
            defaultResponse.setMsg("Survey details updated successfully");
            return ResponseUtils.createSuccessResponse(defaultResponse, new TypeReference<DefaultResponse>() {});

        } catch (SDDException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error while updating survey details", e);
            throw new SDDException(false, "Unexpected error during survey details update");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse<DefaultResponse> updateSurveyStatus(Long userId, SurveyStatusReq request) {
        logger.info("Starting survey status update for ID: {} by user: {}", request.getSurveyId(), userId);

        try {
            SurveyDetails survey = surveyDetailsRepo.findById(request.getSurveyId())
                    .orElseThrow(() -> new SDDException(false, "Survey not found for ID: " + request.getSurveyId()));

            survey.setActive(request.getActive() != null ? request.getActive() : true);
            survey.setUpdatedBy(userId);

            surveyDetailsRepo.save(survey);

            logger.info("Survey status updated successfully for ID: {}", request.getSurveyId());

            DefaultResponse defaultResponse = new DefaultResponse();
            defaultResponse.setMsg("Survey status updated successfully");
            return ResponseUtils.createSuccessResponse(defaultResponse, new TypeReference<DefaultResponse>() {});

        } catch (SDDException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error while updating survey status", e);
            throw new SDDException(false, "Unexpected error during survey status update");
        }
    }

    @Override
    public ApiResponseWithPageable<List<SurveyDetails>> getSurveyDetailsById(GetSurveyReq request) {
        logger.info("Fetching survey details for surveyId: {} or userId: {} with pagination",
                request.getSurveyId(), request.getUserId());

        try {
            Pageable pageable = helperUtils.getPageable(request.getPage(), request.getLimit());
            Page<SurveyDetails> surveyPage;

            if (request.getSurveyId() != null) {
                // 1. If surveyId is provided, wrap single record in a Page
                Optional<SurveyDetails> optionalSurvey = surveyDetailsRepo.findById(request.getSurveyId());

                List<SurveyDetails> content = optionalSurvey.map(List::of).orElse(List.of());
                long totalElements = optionalSurvey.isPresent() ? 1 : 0;

                surveyPage = new PageImpl<>(content, pageable, totalElements);

            } else if (request.getUserId() != null) {
                // 2. If userId is provided, fetch paginated list for that user
                surveyPage = surveyDetailsRepo.findByCreatedBy(request.getUserId(), pageable);

            } else {
                throw new SDDException(false, "Either surveyId or userId must be provided");
            }

            if (surveyPage.getContent().isEmpty()) {
                throw new SDDException(false, "No survey details found");
            }

            logger.info("Survey details fetched successfully..");

            return ResponseUtils.createPageableSuccessResponse(
                    surveyPage.getContent(),
                    surveyPage.getTotalElements(),
                    surveyPage.getTotalPages(),
                    pageable.getPageNumber() + 1,
                    pageable.getPageSize()
            );

        } catch (SDDException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error while fetching survey details", e);
            throw new SDDException(false, "Unexpected error during survey details retrieval");
        }
    }

    @Override
    public ApiResponseWithPageable<List<SurveyDetails>> getAllSurveyDetails(GetSurveyReq request) {
        logger.info("Fetching all survey details with pagination");

        try {
            Pageable pageable = helperUtils.getPageable(request.getPage(), request.getLimit());
            Page<SurveyDetails> surveyPage = surveyDetailsRepo.findAll(pageable);

            logger.info("Survey details fetched successfully");

            return ResponseUtils.createPageableSuccessResponse(
                    surveyPage.getContent(),
                    surveyPage.getTotalElements(),
                    surveyPage.getTotalPages(),
                    pageable.getPageNumber() + 1,
                    pageable.getPageSize()
            );

        } catch (SDDException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error while fetching all survey details", e);
            throw new SDDException(false, "Unexpected error during fetching survey details");
        }
    }



    //**********************TEMPLATE DETAILS******************************/



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
    public ApiResponse<DefaultResponse> updateTemplateDetails(Long userId, UpdateTemplateReq request) {
        logger.info("Starting template details update for ID: {} by user: {}", request.getTemplateId(), userId);

        try {
            // 1. Fetch existing template details
            TemplateDetails templateDetails = templateDetailsRepo.findById(request.getTemplateId())
                    .orElseThrow(() -> new SDDException(false, "Template not found for ID: " + request.getTemplateId()));

            // 2. Validate and fetch parent SurveyDetails entity
            SurveyDetails survey = surveyDetailsRepo.findById(request.getSurveyId())
                    .orElseThrow(() -> new SDDException(false, "Survey not found for ID: " + request.getSurveyId()));

            // 3. Update fields
            templateDetails.setTemplateName(request.getTemplateName());
            templateDetails.setTemplateType(request.getTemplateType());
            templateDetails.setSurvey(survey);
            templateDetails.setUpdatedBy(userId);

            templateDetailsRepo.save(templateDetails);

            logger.info("Template details updated successfully for ID: {}", request.getTemplateId());

            DefaultResponse defaultResponse = new DefaultResponse();
            defaultResponse.setMsg("Template details updated successfully");
            return ResponseUtils.createSuccessResponse(defaultResponse, new TypeReference<DefaultResponse>() {});

        } catch (SDDException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error while updating template details", e);
            throw new SDDException(false, "Unexpected error during template details update");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse<DefaultResponse> updateTemplateStatus(Long userId, TemplateStatusReq request) {
        logger.info("Starting template status update for ID: {} by user: {}", request.getTemplateId(), userId);

        try {
            TemplateDetails templateDetails = templateDetailsRepo.findById(request.getTemplateId())
                    .orElseThrow(() -> new SDDException(false, "Template not found for ID: " + request.getTemplateId()));

            templateDetails.setActive(request.getActive() != null ? request.getActive() : true);
            templateDetails.setUpdatedBy(userId);

            templateDetailsRepo.save(templateDetails);

            logger.info("Template status updated successfully for ID: {}", request.getTemplateId());

            DefaultResponse defaultResponse = new DefaultResponse();
            defaultResponse.setMsg("Template status updated successfully");
            return ResponseUtils.createSuccessResponse(defaultResponse, new TypeReference<DefaultResponse>() {});

        } catch (SDDException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error while updating template status", e);
            throw new SDDException(false, "Unexpected error during template status update");
        }
    }

    @Override
    public ApiResponseWithPageable<List<TemplateDetails>> getTemplateDetailsById(GetTemplateReq request) {
        logger.info("Fetching template details for templateId: {} or userId: {} with pagination",
                request.getTemplateId(), request.getUserId());

        try {
            Pageable pageable = helperUtils.getPageable(request.getPage(), request.getLimit());
            Page<TemplateDetails> templatePage;

            if (request.getTemplateId() != null) {
                Optional<TemplateDetails> optionalTemplate = templateDetailsRepo.findById(request.getTemplateId());
                List<TemplateDetails> content = optionalTemplate.map(List::of).orElse(List.of());
                long totalElements = optionalTemplate.isPresent() ? 1 : 0;
                templatePage = new PageImpl<>(content, pageable, totalElements);

            } else if (request.getUserId() != null) {
                templatePage = templateDetailsRepo.findByCreatedBy(request.getUserId(), pageable);
            } else {
                throw new SDDException(false, "Either templateId or userId must be provided");
            }
            if (templatePage.getContent().isEmpty()) {
                throw new SDDException(false, "No templates found");
            }
            logger.info("Template details fetched successfully.");

            return ResponseUtils.createPageableSuccessResponse(
                    templatePage.getContent(),
                    templatePage.getTotalElements(),
                    templatePage.getTotalPages(),
                    pageable.getPageNumber() + 1,
                    pageable.getPageSize()
            );
        } catch (SDDException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error while fetching template details", e);
            throw new SDDException(false, "Unexpected error during template details retrieval");
        }
    }

    @Override
    public ApiResponseWithPageable<List<TemplateDetails>> getAllTemplateDetails(GetTemplateReq request) {
        logger.info("Fetching all template details with pagination");

        try {
            Pageable pageable = helperUtils.getPageable(request.getPage(), request.getLimit());
            Page<TemplateDetails> templatePage = templateDetailsRepo.findAll(pageable);

            logger.info("Template details fetched successfully");

            return ResponseUtils.createPageableSuccessResponse(
                    templatePage.getContent(),
                    templatePage.getTotalElements(),
                    templatePage.getTotalPages(),
                    pageable.getPageNumber() + 1,
                    pageable.getPageSize()
            );

        } catch (SDDException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error while fetching all template details", e);
            throw new SDDException(false, "Unexpected error during fetching template details");
        }
    }


    //**********************SCHEDULE DETAILS******************************/


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
    public ApiResponse<DefaultResponse> updateScheduleDetails(Long userId, UpdateScheduleReq request) {
        logger.info("Starting schedule details update for ID: {} by user: {}", request.getScheduleId(), userId);

        try {
            // 1. Fetch existing schedule details
            ScheduleDetails scheduleDetails = scheduleDetailsRepo.findById(request.getScheduleId())
                    .orElseThrow(() -> new SDDException(false, "Schedule not found for ID: " + request.getScheduleId()));

            // 2. Validate and fetch parent SurveyDetails entity
            SurveyDetails survey = surveyDetailsRepo.findById(request.getSurveyId())
                    .orElseThrow(() -> new SDDException(false, "Survey not found for ID: " + request.getSurveyId()));

            // 3. Validate and fetch MasScheduleType dependency
            MasScheduleType scheduleType = masScheduleTypeRepo.findById(request.getScheduleTypeId())
                    .orElseThrow(() -> new SDDException(false, "Schedule Type not found for ID: " + request.getScheduleTypeId()));

            // 4. Validate and fetch MasScheduleAttributes dependency
            MasScheduleAttributes scheduleAttributes = masScheduleAttributesRepo.findById(request.getScheduleAttributesId())
                    .orElseThrow(() -> new SDDException(false, "Schedule Attributes not found for ID: " + request.getScheduleAttributesId()));

            // 5. Update fields
            scheduleDetails.setSurvey(survey);
            scheduleDetails.setScheduleType(scheduleType);
            scheduleDetails.setScheduleAttributes(scheduleAttributes);
            scheduleDetails.setUpdatedBy(userId);

            scheduleDetailsRepo.save(scheduleDetails);

            logger.info("Schedule details updated successfully for ID: {}", request.getScheduleId());

            DefaultResponse defaultResponse = new DefaultResponse();
            defaultResponse.setMsg("Schedule details updated successfully");
            return ResponseUtils.createSuccessResponse(defaultResponse, new TypeReference<DefaultResponse>() {});

        } catch (SDDException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error while updating schedule details", e);
            throw new SDDException(false, "Unexpected error during schedule details update");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse<DefaultResponse> updateScheduleStatus(Long userId, ScheduleStatusReq request) {
        logger.info("Starting schedule status update for ID: {} by user: {}", request.getScheduleId(), userId);

        try {
            ScheduleDetails scheduleDetails = scheduleDetailsRepo.findById(request.getScheduleId())
                    .orElseThrow(() -> new SDDException(false, "Schedule not found for ID: " + request.getScheduleId()));

            scheduleDetails.setActive(request.getActive() != null ? request.getActive() : true);
            scheduleDetails.setUpdatedBy(userId);

            scheduleDetailsRepo.save(scheduleDetails);

            logger.info("Schedule status updated successfully for ID: {}", request.getScheduleId());

            DefaultResponse defaultResponse = new DefaultResponse();
            defaultResponse.setMsg("Schedule status updated successfully");
            return ResponseUtils.createSuccessResponse(defaultResponse, new TypeReference<DefaultResponse>() {});

        } catch (SDDException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error while updating schedule status", e);
            throw new SDDException(false, "Unexpected error during schedule status update");
        }
    }

    @Override
    public ApiResponseWithPageable<List<ScheduleDetails>> getScheduleDetailsByIdOrUser(GetScheduleReq request) {
        logger.info("Fetching schedule details for scheduleId: {} or userId: {} with pagination",
                request.getScheduleId(), request.getUserId());

        try {
            Pageable pageable = helperUtils.getPageable(request.getPage(), request.getLimit());
            Page<ScheduleDetails> schedulePage;

            if (request.getScheduleId() != null) {
                // 1. If scheduleId is provided, wrap single record in a Page
                Optional<ScheduleDetails> optionalSchedule = scheduleDetailsRepo.findById(request.getScheduleId());

                List<ScheduleDetails> content = optionalSchedule.map(List::of).orElse(List.of());
                long totalElements = optionalSchedule.isPresent() ? 1 : 0;

                schedulePage = new PageImpl<>(content, pageable, totalElements);

            } else if (request.getUserId() != null) {
                // 2. If userId is provided, fetch paginated list for that user
                schedulePage = scheduleDetailsRepo.findByCreatedBy(request.getUserId(), pageable);

            } else {
                throw new SDDException(false, "Either scheduleId or userId must be provided");
            }

            if (schedulePage.getContent().isEmpty()) {
                throw new SDDException(false, "No schedule details found");
            }

            logger.info("Schedule details fetched successfully");

            return ResponseUtils.createPageableSuccessResponse(
                    schedulePage.getContent(),
                    schedulePage.getTotalElements(),
                    schedulePage.getTotalPages(),
                    pageable.getPageNumber() + 1,
                    pageable.getPageSize()
            );

        } catch (SDDException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error while fetching schedule details", e);
            throw new SDDException(false, "Unexpected error during schedule details retrieval");
        }
    }


    @Override
    public ApiResponseWithPageable<List<ScheduleDetails>> getAllScheduleDetails(GetScheduleReq request) {
        logger.info("Fetching all schedule details with pagination");

        try {
            Pageable pageable = helperUtils.getPageable(request.getPage(), request.getLimit());
            Page<ScheduleDetails> schedulePage = scheduleDetailsRepo.findAll(pageable);

            logger.info("Template schedule fetched successfully");

            return ResponseUtils.createPageableSuccessResponse(
                    schedulePage.getContent(),
                    schedulePage.getTotalElements(),
                    schedulePage.getTotalPages(),
                    pageable.getPageNumber() + 1,
                    pageable.getPageSize()
            );

        } catch (SDDException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error while fetching all schedule details", e);
            throw new SDDException(false, "Unexpected error during fetching schedule details");
        }
    }



    //**********************BLOCK DETAILS******************************/

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
    public ApiResponse<DefaultResponse> updateBlockDetails(Long userId, UpdateBlockReq request) {
        logger.info("Starting block details update for ID: {} by user: {}", request.getBlockId(), userId);

        try {
            // 1. Fetch existing block details
            BlockDetails blockDetails = blockDetailsRepo.findById(request.getBlockId())
                    .orElseThrow(() -> new SDDException(false, "Block not found for ID: " + request.getBlockId()));

            // 2. Validate and fetch parent SurveyDetails entity
            SurveyDetails survey = surveyDetailsRepo.findById(request.getSurveyId())
                    .orElseThrow(() -> new SDDException(false, "Survey not found for ID: " + request.getSurveyId()));

            // 3. Validate and fetch parent ScheduleDetails entity
            ScheduleDetails schedule = scheduleDetailsRepo.findById(request.getScheduleId())
                    .orElseThrow(() -> new SDDException(false, "Schedule not found for ID: " + request.getScheduleId()));

            // 4. Update fields
            blockDetails.setSurvey(survey);
            blockDetails.setSchedule(schedule);
            blockDetails.setBlockHeading(request.getBlockHeading());
            blockDetails.setBlockCode(request.getBlockCode());
            blockDetails.setUpdatedBy(userId);

            blockDetailsRepo.save(blockDetails);

            logger.info("Block details updated successfully for ID: {}", request.getBlockId());

            DefaultResponse defaultResponse = new DefaultResponse();
            defaultResponse.setMsg("Block details updated successfully");
            return ResponseUtils.createSuccessResponse(defaultResponse, new TypeReference<DefaultResponse>() {});

        } catch (SDDException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error while updating block details", e);
            throw new SDDException(false, "Unexpected error during block details update");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse<DefaultResponse> updateBlockStatus(Long userId, BlockStatusReq request) {
        logger.info("Starting block status update for ID: {} by user: {}", request.getBlockId(), userId);

        try {
            BlockDetails blockDetails = blockDetailsRepo.findById(request.getBlockId())
                    .orElseThrow(() -> new SDDException(false, "Block not found for ID: " + request.getBlockId()));

            blockDetails.setActive(request.getActive() != null ? request.getActive() : true);
            blockDetails.setUpdatedBy(userId);

            blockDetailsRepo.save(blockDetails);

            logger.info("Block status updated successfully for ID: {}", request.getBlockId());

            DefaultResponse defaultResponse = new DefaultResponse();
            defaultResponse.setMsg("Block status updated successfully");
            return ResponseUtils.createSuccessResponse(defaultResponse, new TypeReference<DefaultResponse>() {});

        } catch (SDDException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error while updating block status", e);
            throw new SDDException(false, "Unexpected error during block status update");
        }
    }

    @Transactional(readOnly = true)
    @Override
    public ApiResponseWithPageable<List<BlockResp>> getBlockDetailsByIdOrUser(GetBlockReq request) {
        logger.info("Fetching block details with DTO mapping for blockId: {} or userId: {} with pagination",
                request.getBlockId(), request.getUserId());

        try {
            Pageable pageable = helperUtils.getPageable(request.getPage(), request.getLimit());
            Page<BlockDetails> blockPage;

            if (request.getBlockId() != null) {
                Optional<BlockDetails> optionalBlock = blockDetailsRepo.findById(request.getBlockId());
                List<BlockDetails> content = optionalBlock.map(List::of).orElse(List.of());
                long totalElements = optionalBlock.isPresent() ? 1 : 0;
                blockPage = new PageImpl<>(content, pageable, totalElements);

            } else if (request.getUserId() != null) {
                blockPage = blockDetailsRepo.findByCreatedBy(request.getUserId(), pageable);
            } else {
                throw new SDDException(false, "Either blockId or userId must be provided");
            }

            if (blockPage.getContent().isEmpty()) {
                throw new SDDException(false, "No block details found");
            }

            // Map BlockDetails entities to BlockResp DTOs
            List<BlockResp> responseList = blockPage.getContent().stream().map(block ->
                    BlockResp.builder()
                            .blockId(block.getBlockId())
                            .surveyId(block.getSurvey() != null ? block.getSurvey().getSurveyId() : null)
                            .scheduleId(block.getSchedule() != null ? block.getSchedule().getScheduleId() : null)
                            .blockHeading(block.getBlockHeading())
                            .blockCode(block.getBlockCode())
                            .statusId(block.getStatus() != null ? block.getStatus().getStatusId() : null)
                            .statusDescription(block.getStatus() != null ? block.getStatus().getDescription() : null)
                            .active(block.isActive())
                            .createdBy(block.getCreatedBy())
                            .build()
            ).collect(Collectors.toList());

            logger.info("Block details fetched and mapped successfully");

            return ResponseUtils.createPageableSuccessResponse(
                    responseList,
                    blockPage.getTotalElements(),
                    blockPage.getTotalPages(),
                    pageable.getPageNumber() + 1,
                    pageable.getPageSize()
            );

        } catch (SDDException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error while fetching block details", e);
            throw new SDDException(false, "Unexpected error during block details retrieval");
        }
    }


    @Transactional(readOnly = true)
    @Override
    public ApiResponseWithPageable<List<BlockResp>> getAllBlockDetails(GetBlockReq request) {
        logger.info("Fetching all block details with pagination and DTO mapping");

        try {
            Pageable pageable = helperUtils.getPageable(request.getPage(), request.getLimit());
            Page<BlockDetails> blockPage = blockDetailsRepo.findAll(pageable);

            if (blockPage.getContent().isEmpty()) {
                throw new SDDException(false, "No block details found");
            }

            // Map BlockDetails entities to BlockResp DTOs
            List<BlockResp> responseList = blockPage.getContent().stream().map(block ->
                    BlockResp.builder()
                            .blockId(block.getBlockId())
                            .surveyId(block.getSurvey() != null ? block.getSurvey().getSurveyId() : null)
                            .scheduleId(block.getSchedule() != null ? block.getSchedule().getScheduleId() : null)
                            .blockHeading(block.getBlockHeading())
                            .blockCode(block.getBlockCode())
                            .statusId(block.getStatus() != null ? block.getStatus().getStatusId() : null)
                            .statusDescription(block.getStatus() != null ? block.getStatus().getDescription() : null)
                            .active(block.isActive())
                            .createdBy(block.getCreatedBy())
                            .build()
            ).collect(Collectors.toList());

            logger.info("All block details fetched and mapped successfully");

            return ResponseUtils.createPageableSuccessResponse(
                    responseList,
                    blockPage.getTotalElements(),
                    blockPage.getTotalPages(),
                    pageable.getPageNumber() + 1,
                    pageable.getPageSize()
            );

        } catch (SDDException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error while fetching all block details", e);
            throw new SDDException(false, "Unexpected error during block schedule details retrieval");
        }
    }

    //**********************QUESTION DETAILS******************************/

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
                    .dataFormate(request.getDataFormate())
                    .dropDownValue(request.getDropDownValue())
                    .isMandatory(request.isMandatory())
                    .status(masStatusRepo.getReferenceById(1L))
                    .isActive(true)
                    .createdBy(userId)
                    .build();

            questionDetailsRepo.save(questionDetails);
            logger.info("Question details created successfully with ID: {} and number: {}",
                    questionDetails.getQuestionId(), request.getQuestionNumber());

            // 4. Process and Save Skip Conditions (if any)
            if (request.getSkipConditionReq() != null && !request.getSkipConditionReq().isEmpty()) {
                List<SkipCondition> skipConditions = new ArrayList<>();

                for (SkipConditionReq condReq : request.getSkipConditionReq()) {
                    QuestionDetails targetQuestion = null;

                    // If a target question ID is specified, validate and fetch it
                    if (condReq.getQuestionId() != null) {
                        targetQuestion = questionDetailsRepo.findById(condReq.getQuestionId())
                                .orElseThrow(() -> new SDDException(false, "Target question not found for ID: " + condReq.getQuestionId()));
                    }

                    SkipCondition skipCondition = SkipCondition.builder()
                            .question(questionDetails)
                            .targetQuestion(targetQuestion)
                            .condition(condReq.getCondition())
                            .value(condReq.getValue())
                            .isActive(true)
                            .createdBy(userId)
                            .build();

                    skipConditions.add(skipCondition);
                }

                skipConditionRepo.saveAll(skipConditions);
                logger.info("Saved {} skip conditions for question ID: {}", skipConditions.size(), questionDetails.getQuestionId());
            }

            // 5. Process and Save Question Validations (if any)
            if (request.getQuestionValidationReq() != null && !request.getQuestionValidationReq().isEmpty()) {
                List<QuestionValidation> validations = new ArrayList<>();

                for (QuestionValidationReq valReq : request.getQuestionValidationReq()) {
                    QuestionValidation validation = QuestionValidation.builder()
                            .question(questionDetails)
                            .validationType(valReq.getValidationType())
                            .severityCheck(valReq.getSeverityCheck())
                            .value(valReq.getValue())
                            .errorMsg(valReq.getErrorMsg())
                            .isActive(true)
                            .createdBy(userId)
                            .build();

                    validations.add(validation);
                }

                questionValidationRepo.saveAll(validations);
                logger.info("Saved {} validation rows for question ID: {}", validations.size(), questionDetails.getQuestionId());
            }

            // 6. Prepare success response
            DefaultResponse defaultResponse = new DefaultResponse();
            defaultResponse.setMsg("Question details with conditions and validations created successfully");
            return ResponseUtils.createSuccessResponse(defaultResponse, new TypeReference<DefaultResponse>() {});

        } catch (SDDException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error while creating question details", e);
            throw new SDDException(false, "Unexpected error during question details creation");
        }
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse<DefaultResponse> updateQuestionDetails(Long userId, UpdateQuestionReq request) {
        logger.info("Starting question details update for ID: {} by user: {}", request.getQuestionId(), userId);

        try {
            // 1. Fetch existing question details
            QuestionDetails questionDetails = questionDetailsRepo.findById(request.getQuestionId())
                    .orElseThrow(() -> new SDDException(false, "Question not found for ID: " + request.getQuestionId()));

            // 2. Validate and fetch parent SurveyDetails entity
            SurveyDetails survey = surveyDetailsRepo.findById(request.getSurveyId())
                    .orElseThrow(() -> new SDDException(false, "Survey not found for ID: " + request.getSurveyId()));

            // 3. Validate and fetch parent BlockDetails entity
            BlockDetails block = blockDetailsRepo.findById(request.getBlockId())
                    .orElseThrow(() -> new SDDException(false, "Block not found for ID: " + request.getBlockId()));

            // 4. Update core question fields
            questionDetails.setSurvey(survey);
            questionDetails.setBlock(block);
            questionDetails.setQuestionNumber(request.getQuestionNumber());
            questionDetails.setFieldInstructions(request.getFieldInstructions());
            questionDetails.setQuestionHeading(request.getQuestionHeading());
            questionDetails.setDataFormate(request.getDataFormate());
            questionDetails.setDropDownValue(request.getDropDownValue());
            questionDetails.setMandatory(request.isMandatory());
            questionDetails.setUpdatedBy(userId);

            questionDetailsRepo.save(questionDetails);
            logger.info("Question details core info updated successfully for ID: {}", request.getQuestionId());

            // 5. Synchronize Skip Conditions (Upsert pattern)
            if (request.getSkipConditionReq() != null) {
                for (SkipConditionReq condReq : request.getSkipConditionReq()) {
                    SkipCondition skipCondition;

                    if (condReq.getSkipConditionId() != null) {
                        // Update existing skip condition
                        skipCondition = skipConditionRepo.findById(condReq.getSkipConditionId())
                                .orElseThrow(() -> new SDDException(false, "Skip condition not found for ID: " + condReq.getSkipConditionId()));
                        skipCondition.setUpdatedBy(userId);
                    } else {
                        // Create new skip condition if ID is not provided
                        skipCondition = SkipCondition.builder()
                                .question(questionDetails)
                                .isActive(true)
                                .createdBy(userId)
                                .build();
                    }

                    // Fetch target question if specified
                    QuestionDetails targetQuestion = null;
                    if (condReq.getQuestionId() != null) {
                        targetQuestion = questionDetailsRepo.findById(condReq.getQuestionId())
                                .orElseThrow(() -> new SDDException(false, "Target question not found for ID: " + condReq.getQuestionId()));
                    }

                    skipCondition.setTargetQuestion(targetQuestion);
                    skipCondition.setCondition(condReq.getCondition());
                    skipCondition.setValue(condReq.getValue());

                    skipConditionRepo.save(skipCondition);
                }
            }

            // 6. Synchronize Question Validations (Upsert pattern)
            if (request.getQuestionValidationReq() != null) {
                for (QuestionValidationReq valReq : request.getQuestionValidationReq()) {
                    QuestionValidation validation;

                    if (valReq.getValidationId() != null) {
                        // Update existing validation
                        validation = questionValidationRepo.findById(valReq.getValidationId())
                                .orElseThrow(() -> new SDDException(false, "Question validation not found for ID: " + valReq.getValidationId()));
                        validation.setUpdatedBy(userId);
                    } else {
                        // Create new validation if ID is not provided
                        validation = QuestionValidation.builder()
                                .question(questionDetails)
                                .isActive(true)
                                .createdBy(userId)
                                .build();
                    }

                    validation.setValidationType(valReq.getValidationType());
                    validation.setSeverityCheck(valReq.getSeverityCheck());
                    validation.setValue(valReq.getValue());
                    validation.setErrorMsg(valReq.getErrorMsg());

                    questionValidationRepo.save(validation);
                }
            }

            logger.info("Question details, conditions, and validations updated successfully for ID: {}", request.getQuestionId());

            DefaultResponse defaultResponse = new DefaultResponse();
            defaultResponse.setMsg("Question details updated successfully");
            return ResponseUtils.createSuccessResponse(defaultResponse, new TypeReference<DefaultResponse>() {});

        } catch (SDDException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error while updating question details", e);
            throw new SDDException(false, "Unexpected error during question details update");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse<DefaultResponse> updateQuestionStatus(Long userId, QuestionStatusReq request) {
        logger.info("Starting question status update for ID: {} by user: {}", request.getQuestionId(), userId);

        try {
            QuestionDetails questionDetails = questionDetailsRepo.findById(request.getQuestionId())
                    .orElseThrow(() -> new SDDException(false, "Question not found for ID: " + request.getQuestionId()));

            questionDetails.setActive(request.getActive() != null ? request.getActive() : true);
            questionDetails.setUpdatedBy(userId);

            questionDetailsRepo.save(questionDetails);

            logger.info("Question status updated successfully for ID: {}", request.getQuestionId());

            DefaultResponse defaultResponse = new DefaultResponse();
            defaultResponse.setMsg("Question status updated successfully");
            return ResponseUtils.createSuccessResponse(defaultResponse, new TypeReference<DefaultResponse>() {});

        } catch (SDDException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error while updating question status", e);
            throw new SDDException(false, "Unexpected error during question status update");
        }
    }

    @Override
    public ApiResponseWithPageable<List<QuestionResp>> getQuestionDetailsByIdOrUser(GetQuestionReq request) {
        logger.info("Fetching question details with conditions & validations for questionId: {} or userId: {} with pagination",
                request.getQuestionId(), request.getUserId());

        try {
            Pageable pageable = helperUtils.getPageable(request.getPage(), request.getLimit());
            Page<QuestionDetails> questionPage;

            if (request.getQuestionId() != null) {
                Optional<QuestionDetails> optionalQuestion = questionDetailsRepo.findById(request.getQuestionId());
                List<QuestionDetails> content = optionalQuestion.map(List::of).orElse(List.of());
                long totalElements = optionalQuestion.isPresent() ? 1 : 0;
                questionPage = new PageImpl<>(content, pageable, totalElements);

            } else if (request.getUserId() != null) {
                questionPage = questionDetailsRepo.findByCreatedBy(request.getUserId(), pageable);
            } else {
                throw new SDDException(false, "Either questionId or userId must be provided");
            }

            if (questionPage.getContent().isEmpty()) {
                throw new SDDException(false, "No question details found");
            }

            // Map QuestionDetails entities to QuestionResp matching your nested structure
            List<QuestionResp> responseList = questionPage.getContent().stream().map(question -> {

                // Map skip conditions
                List<SkipConditionResp> skipConditions = question.getSkipConditions() != null ?
                        question.getSkipConditions().stream().map(cond -> SkipConditionResp.builder()
                                .skipConditionId(cond.getSkipConditionId())
                                .questionId(cond.getTargetQuestion() != null ? cond.getTargetQuestion().getQuestionId() : null)
                                .condition(cond.getCondition())
                                .value(cond.getValue())
                                .build()).collect(Collectors.toList()) : Collections.emptyList();

                // Map validations
                List<QuestionValidationResp> validations = question.getValidations() != null ?
                        question.getValidations().stream().map(val -> QuestionValidationResp.builder()
                                .validationId(val.getValidationId())
                                .validationType(val.getValidationType())
                                .severityCheck(val.getSeverityCheck())
                                .value(val.getValue())
                                .errorMsg(val.getErrorMsg())
                                .build()).collect(Collectors.toList()) : Collections.emptyList();

                return QuestionResp.builder()
                        .questionId(question.getQuestionId())
                        .surveyId(question.getSurvey() != null ? question.getSurvey().getSurveyId() : null)
                        .blockId(question.getBlock() != null ? question.getBlock().getBlockId() : null)
                        .questionNumber(question.getQuestionNumber())
                        .fieldInstructions(question.getFieldInstructions())
                        .questionHeading(question.getQuestionHeading())
                        .dataFormate(question.getDataFormate())
                        .dropDownValue(question.getDropDownValue())
                        .isMandatory(question.isMandatory())
                        .skipConditionReq(skipConditions)
                        .questionValidationReq(validations)
                        .build();
            }).collect(Collectors.toList());

            logger.info("Question details with conditions and validations mapped and fetched successfully");

            return ResponseUtils.createPageableSuccessResponse(
                    responseList,
                    questionPage.getTotalElements(),
                    questionPage.getTotalPages(),
                    pageable.getPageNumber() + 1,
                    pageable.getPageSize()
            );

        } catch (SDDException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error while fetching question details", e);
            throw new SDDException(false, "Unexpected error during question details retrieval");
        }
    }

    @Override
    public ApiResponseWithPageable<List<QuestionDetails>> getAllQuestionDetails(GetQuestionReq request) {
        logger.info("Fetching all question with pagination");

        try {
            Pageable pageable = helperUtils.getPageable(request.getPage(), request.getLimit());
            Page<QuestionDetails> questionPage = questionDetailsRepo.findAll(pageable);

            logger.info(" question fetched successfully");

            return ResponseUtils.createPageableSuccessResponse(
                    questionPage.getContent(),
                    questionPage.getTotalElements(),
                    questionPage.getTotalPages(),
                    pageable.getPageNumber() + 1,
                    pageable.getPageSize()
            );

        } catch (SDDException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error while fetching all question details", e);
            throw new SDDException(false, "Unexpected error during question details");
        }
    }



}
