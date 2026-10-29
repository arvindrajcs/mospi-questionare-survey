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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class SurveyServiceImpl implements SurveyService {

    private final Logger logger = LoggerFactory.getLogger(SurveyServiceImpl.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

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

    @Autowired
    private UserInfoRepo userInfoRepo;

    @Autowired
    private MappingUserRoleRepo mappingUserRoleRepo;

    @Autowired
    private AssignDetailsRepo assignDetailsRepo;



    //**********************SURVEY DETAILS******************************/

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse<SurveyCreateResp> createSurveyDetails(Long userId, CreateSurveyReq request) {
        logger.info("Starting survey design creation for user: {}", userId);

        try {
            boolean hasPendingOrDraftSurvey = surveyDetailsRepo.existsByCreatedByAndStatus_StatusIdIn(userId, List.of(1L, 3L));
            if (hasPendingOrDraftSurvey) {
                throw new SDDException(false, "You cannot create a new survey while you have an existing survey in Draft or Pending status.");
            }

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

            SurveyCreateResp defaultResponse = new SurveyCreateResp();
            defaultResponse.setMsg("Survey design created successfully");
            defaultResponse.setSurveyId(survey.getSurveyId());
            return ResponseUtils.createSuccessResponse(defaultResponse, new TypeReference<SurveyCreateResp>() {});

        } catch (SDDException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error while creating survey design", e);
            throw new SDDException(false, "Unexpected error during survey design creation");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse<SurveyCreateResp> updateSurveyDetails(Long userId, UpdateSurveyReq request) {
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

            SurveyCreateResp defaultResponse = new SurveyCreateResp();
            defaultResponse.setMsg("Survey details updated successfully");
            defaultResponse.setSurveyId(survey.getSurveyId());
            return ResponseUtils.createSuccessResponse(defaultResponse, new TypeReference<SurveyCreateResp>() {});

        } catch (SDDException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error while updating survey details", e);
            throw new SDDException(false, "Unexpected error during survey details update");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse<SurveyCreateResp> updateSurveyStatus(Long userId, SurveyStatusReq request) {
        logger.info("Starting survey status update for ID: {} by user: {}", request.getSurveyId(), userId);

        try {
            SurveyDetails survey = surveyDetailsRepo.findById(request.getSurveyId())
                    .orElseThrow(() -> new SDDException(false, "Survey not found for ID: " + request.getSurveyId()));

            survey.setActive(request.getActive() != null ? request.getActive() : true);
            survey.setUpdatedBy(userId);

            surveyDetailsRepo.save(survey);

            logger.info("Survey status updated successfully for ID: {}", request.getSurveyId());

            SurveyCreateResp defaultResponse = new SurveyCreateResp();
            defaultResponse.setMsg("Survey status updated successfully");
            defaultResponse.setSurveyId(survey.getSurveyId());
            return ResponseUtils.createSuccessResponse(defaultResponse, new TypeReference<SurveyCreateResp>() {});

        } catch (SDDException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error while updating survey status", e);
            throw new SDDException(false, "Unexpected error during survey status update");
        }
    }

    @Transactional(readOnly = true)
    @Override
    public ApiResponseWithPageable<List<SurveyResp>> getSurveyDetailsById(GetSurveyReq request) {
        logger.info("Fetching survey details with DTO mapping for surveyId: {} or userId: {} with pagination",
                request.getSurveyId(), request.getUserId());

        try {
            Pageable pageable = helperUtils.getPageable(request.getPage(), request.getLimit());
            Page<SurveyDetails> surveyPage;

            if (request.getSurveyId() != null) {
                Optional<SurveyDetails> optionalSurvey = surveyDetailsRepo.findById(request.getSurveyId());
                List<SurveyDetails> content = optionalSurvey.map(List::of).orElse(List.of());
                long totalElements = optionalSurvey.isPresent() ? 1 : 0;
                surveyPage = new PageImpl<>(content, pageable, totalElements);

            } else if (request.getUserId() != null) {
                surveyPage = surveyDetailsRepo.findByCreatedBy(request.getUserId(), pageable);
            } else {
                throw new SDDException(false, "Either surveyId or userId must be provided");
            }

            if (surveyPage.getContent().isEmpty()) {
                throw new SDDException(false, "No survey details found");
            }

            // Map SurveyDetails entities to SurveyResp DTOs
            List<SurveyResp> responseList = surveyPage.getContent().stream().map(survey ->
                    SurveyResp.builder()
                            .surveyId(survey.getSurveyId())
                            .title(survey.getTitle())
                            .periodicCycle(survey.getPeriodicCycle())
                            .surveyCode(survey.getSurveyCode())
                            .divisionId(survey.getDivision() != null ? survey.getDivision().getDivisionId() : null)
                            .divisionDescription(survey.getDivision() != null ? survey.getDivision().getDescription() : null)
                            .enquiryTypeId(survey.getEnquiryType() != null ? survey.getEnquiryType().getEnquiryTypeId() : null)
                            .enquiryTypeDescription(survey.getEnquiryType() != null ? survey.getEnquiryType().getDescription() : null)
                            .frequencyTypeId(survey.getFrequencyType() != null ? survey.getFrequencyType().getFrequencyTypeId() : null)
                            .frequencyTypeDescription(survey.getFrequencyType() != null ? survey.getFrequencyType().getDescription() : null)
                            .seasonalityFactorId(survey.getSeasonalityFactor() != null ? survey.getSeasonalityFactor().getSeasonalityFactorId() : null)
                            .seasonalityFactorDescription(survey.getSeasonalityFactor() != null ? survey.getSeasonalityFactor().getDescription() : null)
                            .surveyDuration(survey.getSurveyDuration())
                            .ruralSamplingFrame(survey.getRuralSamplingFrame())
                            .urbanSamplingFrame(survey.getUrbanSamplingFrame())
                            .samplingSize(survey.getSamplingSize())
                            .surveyObjective(survey.getSurveyObjective())
                            .remarks(survey.getRemarks())
                            .statusId(survey.getStatus() != null ? survey.getStatus().getStatusId() : null)
                            .statusDescription(survey.getStatus() != null ? survey.getStatus().getDescription() : null)
                            .isActive(survey.isActive())
                            .createdBy(survey.getCreatedBy())
                            .build()
            ).collect(Collectors.toList());

            logger.info("Survey details fetched and mapped successfully.");

            return ResponseUtils.createPageableSuccessResponse(
                    responseList,
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

    @Transactional(readOnly = true)
    @Override
    public ApiResponseWithPageable<List<SurveyResp>> getAllSurveyDetails(Long userId,GetSurveyReq request) {
        logger.info("Fetching all survey details with pagination and DTO mapping");

        try {
            if (userId == null) {
                throw new SDDException(false, "User ID must be provided");
            }
            UserInfo users = userInfoRepo.findById(userId)
                    .orElseThrow(() -> new SDDException(false, "Invalid User, User not found for ID: " + userId));


            Pageable pageable = helperUtils.getPageable(request.getPage(), request.getLimit());
            Page<SurveyDetails> surveyPage = surveyDetailsRepo.findByCreatedBy(userId,pageable);

            if (surveyPage.getContent().isEmpty()) {
                throw new SDDException(false, "No survey details found");
            }

            // Map SurveyDetails entities to SurveyResp DTOs
            List<SurveyResp> responseList = surveyPage.getContent().stream().map(survey ->
                    SurveyResp.builder()
                            .surveyId(survey.getSurveyId())
                            .title(survey.getTitle())
                            .periodicCycle(survey.getPeriodicCycle())
                            .surveyCode(survey.getSurveyCode())
                            .divisionId(survey.getDivision() != null ? survey.getDivision().getDivisionId() : null)
                            .divisionDescription(survey.getDivision() != null ? survey.getDivision().getDescription() : null)
                            .enquiryTypeId(survey.getEnquiryType() != null ? survey.getEnquiryType().getEnquiryTypeId() : null)
                            .enquiryTypeDescription(survey.getEnquiryType() != null ? survey.getEnquiryType().getDescription() : null)
                            .frequencyTypeId(survey.getFrequencyType() != null ? survey.getFrequencyType().getFrequencyTypeId() : null)
                            .frequencyTypeDescription(survey.getFrequencyType() != null ? survey.getFrequencyType().getDescription() : null)
                            .seasonalityFactorId(survey.getSeasonalityFactor() != null ? survey.getSeasonalityFactor().getSeasonalityFactorId() : null)
                            .seasonalityFactorDescription(survey.getSeasonalityFactor() != null ? survey.getSeasonalityFactor().getDescription() : null)
                            .surveyDuration(survey.getSurveyDuration())
                            .ruralSamplingFrame(survey.getRuralSamplingFrame())
                            .urbanSamplingFrame(survey.getUrbanSamplingFrame())
                            .samplingSize(survey.getSamplingSize())
                            .surveyObjective(survey.getSurveyObjective())
                            .remarks(survey.getRemarks())
                            .statusId(survey.getStatus() != null ? survey.getStatus().getStatusId() : null)
                            .statusDescription(survey.getStatus() != null ? survey.getStatus().getDescription() : null)
                            .isActive(survey.isActive())
                            .createdBy(survey.getCreatedBy())
                            .build()
            ).collect(Collectors.toList());

            logger.info("All survey details fetched and mapped successfully");

            return ResponseUtils.createPageableSuccessResponse(
                    responseList,
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

    @Transactional(readOnly = true)
    @Override
    public ApiResponseWithPageable<List<TemplateResp>> getTemplateDetailsById(GetTemplateReq request) {
        logger.info("Fetching template details with DTO mapping for templateId: {}, userId: {}, or surveyId: {} with pagination",
                request.getTemplateId(), request.getUserId(), request.getSurveyId());

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
            } else if (request.getSurveyId() != null) {
                templatePage = templateDetailsRepo.findBySurvey_SurveyId(request.getSurveyId(), pageable);
            } else {
                throw new SDDException(false, "Either templateId, userId, or surveyId must be provided");
            }

            if (templatePage.getContent().isEmpty()) {
                throw new SDDException(false, "No templates found");
            }

            // Map TemplateDetails entities to TemplateResp DTOs
            List<TemplateResp> responseList = templatePage.getContent().stream().map(template ->
                    TemplateResp.builder()
                            .templateId(template.getTemplateId())
                            .templateName(template.getTemplateName())
                            .templateType(template.getTemplateType())
                            .surveyId(template.getSurvey() != null ? template.getSurvey().getSurveyId() : null)
                            .isActive(template.isActive())
                            .createdBy(template.getCreatedBy())
                            .build()
            ).collect(Collectors.toList());

            logger.info("Template details fetched and mapped successfully.");

            return ResponseUtils.createPageableSuccessResponse(
                    responseList,
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

    @Transactional(readOnly = true)
    @Override
    public ApiResponseWithPageable<List<TemplateResp>> getAllTemplateDetails(GetTemplateReq request) {
        logger.info("Fetching all template details with pagination and DTO mapping");

        try {
            Pageable pageable = helperUtils.getPageable(request.getPage(), request.getLimit());
            Page<TemplateDetails> templatePage = templateDetailsRepo.findAll(pageable);

            if (templatePage.getContent().isEmpty()) {
                throw new SDDException(false, "No template details found");
            }

            // Map TemplateDetails entities to TemplateResp DTOs
            List<TemplateResp> responseList = templatePage.getContent().stream().map(template ->
                    TemplateResp.builder()
                            .templateId(template.getTemplateId())
                            .templateName(template.getTemplateName())
                            .templateType(template.getTemplateType())
                            .surveyId(template.getSurvey() != null ? template.getSurvey().getSurveyId() : null)
                            .isActive(template.isActive())
                            .createdBy(template.getCreatedBy())
                            .build()
            ).collect(Collectors.toList());

            logger.info("All template details fetched and mapped successfully");

            return ResponseUtils.createPageableSuccessResponse(
                    responseList,
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
    public ApiResponse<ScheduleCreationResp> createScheduleDetails(Long userId, CreateScheduleReq request) {
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
                    .isActive(true)
                    .createdBy(userId)
                    .build();

            scheduleDetailsRepo.save(scheduleDetails);

            logger.info("Schedule details created successfully for survey ID: {}", request.getSurveyId());

            // 5. Prepare success response
            ScheduleCreationResp defaultResponse = new ScheduleCreationResp();
            defaultResponse.setMsg("Schedule details created successfully");
            defaultResponse.setScheduleId(scheduleDetails.getScheduleId());
            return ResponseUtils.createSuccessResponse(defaultResponse, new TypeReference<ScheduleCreationResp>() {});

        } catch (SDDException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error while creating schedule details", e);
            throw new SDDException(false, "Unexpected error during schedule details creation");
        }
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse<ScheduleCreationResp> updateScheduleDetails(Long userId, UpdateScheduleReq request) {
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

            ScheduleCreationResp defaultResponse = new ScheduleCreationResp();
            defaultResponse.setMsg("Schedule details updated successfully");
            defaultResponse.setScheduleId(scheduleDetails.getScheduleId());
            return ResponseUtils.createSuccessResponse(defaultResponse, new TypeReference<ScheduleCreationResp>() {});

        } catch (SDDException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error while updating schedule details", e);
            throw new SDDException(false, "Unexpected error during schedule details update");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse<ScheduleCreationResp> updateScheduleStatus(Long userId, ScheduleStatusReq request) {
        logger.info("Starting schedule status update for ID: {} by user: {}", request.getScheduleId(), userId);

        try {
            ScheduleDetails scheduleDetails = scheduleDetailsRepo.findById(request.getScheduleId())
                    .orElseThrow(() -> new SDDException(false, "Schedule not found for ID: " + request.getScheduleId()));

            scheduleDetails.setActive(request.getActive() != null ? request.getActive() : true);
            scheduleDetails.setUpdatedBy(userId);

            scheduleDetailsRepo.save(scheduleDetails);

            logger.info("Schedule status updated successfully for ID: {}", request.getScheduleId());

            ScheduleCreationResp defaultResponse = new ScheduleCreationResp();
            defaultResponse.setMsg("Schedule status updated successfully");
            defaultResponse.setScheduleId(scheduleDetails.getScheduleId());
            return ResponseUtils.createSuccessResponse(defaultResponse, new TypeReference<ScheduleCreationResp>() {});

        } catch (SDDException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error while updating schedule status", e);
            throw new SDDException(false, "Unexpected error during schedule status update");
        }
    }

    @Transactional(readOnly = true)
    @Override
    public ApiResponseWithPageable<List<ScheduleResp>> getScheduleDetailsByIdOrUser(GetScheduleReq request) {
        logger.info("Fetching schedule details with DTO mapping for scheduleId: {}, userId: {}, or surveyId: {} with pagination",
                request.getScheduleId(), request.getUserId(), request.getSurveyId());

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

            } else if (request.getSurveyId() != null) {
                // 3. If surveyId is provided, fetch paginated list for that survey
                schedulePage = scheduleDetailsRepo.findBySurvey_SurveyId(request.getSurveyId(), pageable);
            } else {
                throw new SDDException(false, "Either scheduleId, userId, or surveyId must be provided");
            }

            if (schedulePage.getContent().isEmpty()) {
                throw new SDDException(false, "No schedule details found");
            }

            // Map ScheduleDetails entities to ScheduleResp DTOs
            List<ScheduleResp> responseList = schedulePage.getContent().stream().map(schedule ->
                    ScheduleResp.builder()
                            .scheduleId(schedule.getScheduleId())
                            .surveyId(schedule.getSurvey() != null ? schedule.getSurvey().getSurveyId() : null)
                            .scheduleTypeId(schedule.getScheduleType() != null ? schedule.getScheduleType().getScheduleTypeId() : null)
                            .scheduleTypeDescription(schedule.getScheduleType() != null ? schedule.getScheduleType().getDescription() : null)
                            .scheduleAttributesId(schedule.getScheduleAttributes() != null ? schedule.getScheduleAttributes().getScheduleAttributesId() : null)
                            .scheduleAttributesDescription(schedule.getScheduleAttributes() != null ? schedule.getScheduleAttributes().getDescription() : null)
                            .isActive(schedule.isActive())
                            .createdBy(schedule.getCreatedBy())
                            .build()
            ).collect(Collectors.toList());

            logger.info("Schedule details fetched and mapped successfully");

            return ResponseUtils.createPageableSuccessResponse(
                    responseList,
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


    @Transactional(readOnly = true)
    @Override
    public ApiResponseWithPageable<List<ScheduleResp>> getAllScheduleDetails(GetScheduleReq request) {
        logger.info("Fetching all schedule details with pagination and DTO mapping");

        try {
            Pageable pageable = helperUtils.getPageable(request.getPage(), request.getLimit());
            Page<ScheduleDetails> schedulePage = scheduleDetailsRepo.findAll(pageable);

            if (schedulePage.getContent().isEmpty()) {
                throw new SDDException(false, "No schedule details found");
            }

            // Map ScheduleDetails entities to ScheduleResp DTOs
            List<ScheduleResp> responseList = schedulePage.getContent().stream().map(schedule ->
                    ScheduleResp.builder()
                            .scheduleId(schedule.getScheduleId())
                            .surveyId(schedule.getSurvey() != null ? schedule.getSurvey().getSurveyId() : null)
                            .scheduleTypeId(schedule.getScheduleType() != null ? schedule.getScheduleType().getScheduleTypeId() : null)
                            .scheduleTypeDescription(schedule.getScheduleType() != null ? schedule.getScheduleType().getDescription() : null)
                            .scheduleAttributesId(schedule.getScheduleAttributes() != null ? schedule.getScheduleAttributes().getScheduleAttributesId() : null)
                            .scheduleAttributesDescription(schedule.getScheduleAttributes() != null ? schedule.getScheduleAttributes().getDescription() : null)
                            .isActive(schedule.isActive())
                            .createdBy(schedule.getCreatedBy())
                            .build()
            ).collect(Collectors.toList());

            logger.info("All schedule details fetched and mapped successfully");

            return ResponseUtils.createPageableSuccessResponse(
                    responseList,
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
//            boolean hasPendingOrDraftSurvey = blockDetailsRepo.existsByCreatedByAndStatus_StatusIdIn(userId, List.of(1L, 3L));
//            if (hasPendingOrDraftSurvey) {
//                throw new SDDException(false, "You cannot create a new survey block while you have an existing survey in Draft or Pending status.");
//            }

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
            } else if (request.getSurveyId() != null){
                blockPage = blockDetailsRepo.findBySurvey_SurveyId(request.getSurveyId(), pageable);
            }else {
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

//            boolean hasPendingOrDraftSurvey = questionDetailsRepo.existsByCreatedByAndStatus_StatusIdIn(userId, List.of(1L, 3L));
//            if (hasPendingOrDraftSurvey) {
//                throw new SDDException(false, "You cannot create a new survey question under block while you have an existing survey in Draft or Pending status.");
//            }
            // 1. Validate and fetch parent SurveyDetails entity
            SurveyDetails survey = surveyDetailsRepo.findById(request.getSurveyId())
                    .orElseThrow(() -> new SDDException(false, "Survey not found for ID: " + request.getSurveyId()));

            // 2. Validate and fetch parent BlockDetails entity
            BlockDetails block = blockDetailsRepo.findById(request.getBlockId())
                    .orElseThrow(() -> new SDDException(false, "Block not found for ID: " + request.getBlockId()));

            String surveyCode = (survey != null && survey.getSurveyCode() != null)
                    ? survey.getSurveyCode().replaceAll("\\s+", "").toUpperCase() : "";

            String blockCode = (block != null && block.getBlockCode() != null)
                    ? block.getBlockCode().replaceAll("\\s+", "").toUpperCase() : "";

            String questionNum = (request != null && request.getQuestionNumber() != null)
                    ? request.getQuestionNumber().replaceAll("\\s+", "").toUpperCase() : "";
            String questionCode = surveyCode + blockCode + questionNum;

            // 3. Build and save QuestionDetails
            QuestionDetails questionDetails = QuestionDetails.builder()
                    .questionCode(questionCode)
                    .survey(survey)
                    .block(block)
                    .questionNumber(request.getQuestionNumber())
                    .fieldInstructions(request.getFieldInstructions())
                    .questionHeading(request.getQuestionHeading())
                    .dataFormate(request.getDataFormate())
                    .dropDownValue(request.getDropDownValue())
                    .isMandatory(request.isMandatory())
                    .questionCondition(request.getQuestionCondition())
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
            questionDetails.setQuestionCondition(request.getQuestionCondition());
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
            } else if (request.getSurveyId() != null){
                questionPage = questionDetailsRepo.findBySurvey_SurveyId(request.getSurveyId(), pageable);
            }else {
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
                        .questionCode(question.getQuestionCode())
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
                        .questionCondition(question.getQuestionCondition())
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

//    @Transactional(readOnly = true)
//    @Override
//    public ApiResponseWithPageable<List<QuestionResp>> getAllQuestionDetails(GetQuestionReq request) {
//        logger.info("Fetching all question details with conditions & validations and pagination");
//
//        try {
//            Pageable pageable = helperUtils.getPageable(request.getPage(), request.getLimit());
//            Page<QuestionDetails> questionPage = questionDetailsRepo.findAll(pageable);
//
//            if (questionPage.getContent().isEmpty()) {
//                throw new SDDException(false, "No question details found");
//            }
//
//            // Map QuestionDetails entities to QuestionResp matching your nested structure
//            List<QuestionResp> responseList = questionPage.getContent().stream().map(question -> {
//
//                // Map skip conditions
//                List<SkipConditionResp> skipConditions = question.getSkipConditions() != null ?
//                        question.getSkipConditions().stream().map(cond -> SkipConditionResp.builder()
//                                .skipConditionId(cond.getSkipConditionId())
//                                .questionId(cond.getTargetQuestion() != null ? cond.getTargetQuestion().getQuestionId() : null)
//                                .condition(cond.getCondition())
//                                .value(cond.getValue())
//                                .build()).collect(Collectors.toList()) : Collections.emptyList();
//
//                // Map validations
//                List<QuestionValidationResp> validations = question.getValidations() != null ?
//                        question.getValidations().stream().map(val -> QuestionValidationResp.builder()
//                                .validationId(val.getValidationId())
//                                .validationType(val.getValidationType())
//                                .severityCheck(val.getSeverityCheck())
//                                .value(val.getValue())
//                                .errorMsg(val.getErrorMsg())
//                                .build()).collect(Collectors.toList()) : Collections.emptyList();
//
//                return QuestionResp.builder()
//                        .questionId(question.getQuestionId())
//                        .questionCode(question.getQuestionCode())
//                        .surveyId(question.getSurvey() != null ? question.getSurvey().getSurveyId() : null)
//                        .blockId(question.getBlock() != null ? question.getBlock().getBlockId() : null)
//                        .questionNumber(question.getQuestionNumber())
//                        .fieldInstructions(question.getFieldInstructions())
//                        .questionHeading(question.getQuestionHeading())
//                        .dataFormate(question.getDataFormate())
//                        .dropDownValue(question.getDropDownValue())
//                        .isMandatory(question.isMandatory())
//                        .skipConditionReq(skipConditions)
//                        .questionValidationReq(validations)
//                        .questionCondition(question.getQuestionCondition())
//                        .build();
//            }).collect(Collectors.toList());
//
//            logger.info("All question details with conditions and validations mapped and fetched successfully");
//
//            return ResponseUtils.createPageableSuccessResponse(
//                    responseList,
//                    questionPage.getTotalElements(),
//                    questionPage.getTotalPages(),
//                    pageable.getPageNumber() + 1,
//                    pageable.getPageSize()
//            );
//
//        } catch (SDDException e) {
//            throw e;
//        } catch (Exception e) {
//            logger.error("Error while fetching all question details", e);
//            throw new SDDException(false, "Unexpected error during all question details retrieval");
//        }
//    }



    @Transactional(readOnly = true)
    @Override
    public ApiResponseWithPageable<List<QuestionResp>> getAllQuestionDetails(GetQuestionReq request) {
        logger.info("Fetching all question details with conditions & validations");

        try {
            List<QuestionDetails> questionList;
            long totalElements;
            int totalPages;
            int pageNumber;
            int pageSize;

            // Check if page or limit is null/zero/negative
            boolean isUnpaged = request == null ||
                    request.getPage() == null || request.getPage() <= 0 ||
                    request.getLimit() == null || request.getLimit() <= 0;

            if (isUnpaged) {
                // Scenario 1: Fetch all records without pagination
                questionList = questionDetailsRepo.findAll();

                if (questionList.isEmpty()) {
                    throw new SDDException(false, "No question details found");
                }

                totalElements = questionList.size();
                totalPages = 1;
                pageNumber = 1;
                pageSize = (int) totalElements; // Or whatever default size you prefer

            } else {
                // Scenario 2: Fetch records with pagination
                Pageable pageable = helperUtils.getPageable(request.getPage(), request.getLimit());
                Page<QuestionDetails> questionPage = questionDetailsRepo.findAll(pageable);

                if (questionPage.getContent().isEmpty()) {
                    throw new SDDException(false, "No question details found");
                }

                questionList = questionPage.getContent();
                totalElements = questionPage.getTotalElements();
                totalPages = questionPage.getTotalPages();
                pageNumber = pageable.getPageNumber() + 1;
                pageSize = pageable.getPageSize();
            }

            // Map QuestionDetails entities to QuestionResp matching your nested structure
            List<QuestionResp> responseList = questionList.stream().map(question -> {

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
                        .questionCode(question.getQuestionCode())
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
                        .questionCondition(question.getQuestionCondition())
                        .build();
            }).collect(Collectors.toList());

            logger.info("All question details mapped and fetched successfully (Unpaged: {})", isUnpaged);

            return ResponseUtils.createPageableSuccessResponse(
                    responseList,
                    totalElements,
                    totalPages,
                    pageNumber,
                    pageSize
            );

        } catch (SDDException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error while fetching all question details", e);
            throw new SDDException(false, "Unexpected error during all question details retrieval");
        }
    }



    //**********************TEST SURVEY******************************/


    @Transactional(readOnly = true)
    @Override
    public ApiResponse<TestSurveyResp> getTestSurveyDetails(TestSurveyReq request) {
        logger.info("Fetching complete test survey details for surveyId: {}", request.getSurveyId());

        try {
            if (request.getSurveyId() == null) {
                throw new SDDException(false, "Survey ID must be provided");
            }

            // 1. Fetch SurveyDetails
            SurveyDetails survey = surveyDetailsRepo.findById(request.getSurveyId())
                    .orElseThrow(() -> new SDDException(false, "Survey not found for ID: " + request.getSurveyId()));

            // 2. Fetch and map related Templates
            List<TemplateDetails> templates = templateDetailsRepo.findAllBySurvey_SurveyId(request.getSurveyId());
            ArrayList<TemplateResp> templateResps = templates.stream().map(template ->
                    TemplateResp.builder()
                            .templateId(template.getTemplateId())
                            .templateName(template.getTemplateName())
                            .templateType(template.getTemplateType())
                            .surveyId(template.getSurvey() != null ? template.getSurvey().getSurveyId() : null)
                            .isActive(template.isActive())
                            .createdBy(template.getCreatedBy())
                            .build()
            ).collect(Collectors.toCollection(ArrayList::new));

            // 3. Fetch and map related Schedules
            List<ScheduleDetails> schedules = scheduleDetailsRepo.findAllBySurvey_SurveyId(request.getSurveyId());
            ArrayList<ScheduleResp> scheduleResps = schedules.stream().map(schedule ->
                    ScheduleResp.builder()
                            .scheduleId(schedule.getScheduleId())
                            .surveyId(schedule.getSurvey() != null ? schedule.getSurvey().getSurveyId() : null)
                            .scheduleTypeId(schedule.getScheduleType() != null ? schedule.getScheduleType().getScheduleTypeId() : null)
                            .scheduleTypeDescription(schedule.getScheduleType() != null ? schedule.getScheduleType().getDescription() : null)
                            .scheduleAttributesId(schedule.getScheduleAttributes() != null ? schedule.getScheduleAttributes().getScheduleAttributesId() : null)
                            .scheduleAttributesDescription(schedule.getScheduleAttributes() != null ? schedule.getScheduleAttributes().getDescription() : null)
                            .isActive(schedule.isActive())
                            .createdBy(schedule.getCreatedBy())
                            .build()
            ).collect(Collectors.toCollection(ArrayList::new));

            // 4. Fetch and map related Blocks
            List<BlockDetails> blocks = blockDetailsRepo.findAllBySurvey_SurveyId(request.getSurveyId());
            ArrayList<BlockResp> blockResps = blocks.stream().map(block ->
                    BlockResp.builder()
                            .blockId(block.getBlockId())
                            .surveyId(block.getSurvey() != null ? block.getSurvey().getSurveyId() : null)
                            .scheduleId(block.getSchedule() != null ? block.getSchedule().getScheduleId() : null)
                            .blockHeading(block.getBlockHeading())
                            .blockCode(block.getBlockCode())
                            .active(block.isActive())
                            .createdBy(block.getCreatedBy())
                            .build()
            ).collect(Collectors.toCollection(ArrayList::new));

            // 5. Fetch and map related Questions with Skip Conditions & Validations
            List<QuestionDetails> questions = questionDetailsRepo.findAllBySurvey_SurveyId(request.getSurveyId());
            ArrayList<QuestionResp> questionResps = questions.stream().map(question -> {

                List<SkipConditionResp> skipConditions = question.getSkipConditions() != null ?
                        question.getSkipConditions().stream().map(cond -> SkipConditionResp.builder()
                                .skipConditionId(cond.getSkipConditionId())
                                .questionId(cond.getTargetQuestion() != null ? cond.getTargetQuestion().getQuestionId() : null)
                                .condition(cond.getCondition())
                                .value(cond.getValue())
                                .build()).collect(Collectors.toList()) : Collections.emptyList();

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
                        .questionCode(question.getQuestionCode())
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
                        .questionCondition(question.getQuestionCondition())
                        .build();
            }).collect(Collectors.toCollection(ArrayList::new));

            // 6. Build the aggregated TestSurveyResp object
            TestSurveyResp response = TestSurveyResp.builder()
                    .surveyId(survey.getSurveyId())
                    .title(survey.getTitle())
                    .periodicCycle(survey.getPeriodicCycle())
                    .surveyCode(survey.getSurveyCode())
                    .divisionId(survey.getDivision() != null ? survey.getDivision().getDivisionId() : null)
                    .divisionDescription(survey.getDivision() != null ? survey.getDivision().getDescription() : null)
                    .enquiryTypeId(survey.getEnquiryType() != null ? survey.getEnquiryType().getEnquiryTypeId() : null)
                    .enquiryTypeDescription(survey.getEnquiryType() != null ? survey.getEnquiryType().getDescription() : null)
                    .frequencyTypeId(survey.getFrequencyType() != null ? survey.getFrequencyType().getFrequencyTypeId() : null)
                    .frequencyTypeDescription(survey.getFrequencyType() != null ? survey.getFrequencyType().getDescription() : null)
                    .seasonalityFactorId(survey.getSeasonalityFactor() != null ? survey.getSeasonalityFactor().getSeasonalityFactorId() : null)
                    .seasonalityFactorDescription(survey.getSeasonalityFactor() != null ? survey.getSeasonalityFactor().getDescription() : null)
                    .surveyDuration(survey.getSurveyDuration())
                    .ruralSamplingFrame(survey.getRuralSamplingFrame())
                    .urbanSamplingFrame(survey.getUrbanSamplingFrame())
                    .samplingSize(survey.getSamplingSize())
                    .surveyObjective(survey.getSurveyObjective())
                    .remarks(survey.getRemarks())
                    .statusId(survey.getStatus() != null ? survey.getStatus().getStatusId() : null)
                    .statusDescription(survey.getStatus() != null ? survey.getStatus().getDescription() : null)
                    .isActive(survey.isActive())
                    .createdBy(survey.getCreatedBy())
                    .templateResp(templateResps)
                    .scheduleResp(scheduleResps)
                    .blockResp(blockResps)
                    .questionResp(questionResps)
                    .build();

            logger.info("Test survey details aggregated successfully for surveyId: {}", request.getSurveyId());

            return ResponseUtils.createSuccessResponse(response, new TypeReference<TestSurveyResp>() {});

        } catch (SDDException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error while fetching test survey details for surveyId: {}", request.getSurveyId(), e);
            throw new SDDException(false, "Unexpected error during test survey details retrieval");
        }
    }



    @Transactional(readOnly = true)
    @Override
    public ApiResponseWithPageable<List<TestSurveyResp>> getSurveyDetailsByStatus(Long userId, GetSurveyByStatusReq request) {
        logger.info("Fetching paginated survey details using survey status: {}", request.getStatus());

        try {

            if (userId == null) {
                throw new SDDException(false, "User ID must be provided");
            }
            UserInfo users = userInfoRepo.findById(userId)
                    .orElseThrow(() -> new SDDException(false, "Invalid User, User not found for ID: " + userId));

            List<MappingUserRole> userRole =mappingUserRoleRepo.findByUser(users);

            List<String> validStatuses = List.of("Draft", "Pending", "Approved", "Publish", "All");

            if (request.getStatus() == null ||
                    validStatuses.stream().noneMatch(s -> s.equalsIgnoreCase(request.getStatus()))) {
                throw new SDDException(false, "Invalid status. Status must be Draft, Pending, Approved, or Publish or All");
            }


            Long statusId = switch (request.getStatus().toLowerCase()) {
                case "draft" -> 1L;
                case "pending" -> 2L;
                case "approved" -> 3L;
                case "publish" -> 4L;
                case "all" -> 0L;
                default -> throw new SDDException(false, "Unsupported status mapping");
            };

            Page<SurveyDetails> surveyPage;
            Pageable pageable = helperUtils.getPageable(request.getPage(), request.getLimit());

            if (statusId ==0){
                surveyPage = surveyDetailsRepo.findByCreatedBy(userId, pageable);
            }else{
                surveyPage = surveyDetailsRepo.findByStatus_StatusIdAndCreatedBy(statusId, userId, pageable);
            }

            if (surveyPage.getContent().isEmpty()) {
                throw new SDDException(false, "No survey details found for status and for this User: " + request.getStatus() + ":" + userId);
            }

            // 5. Map each SurveyDetails entity into TestSurveyResp with all related details
            List<TestSurveyResp> responseList = surveyPage.getContent().stream().map(survey -> {
                Long surveyId = survey.getSurveyId();

                // Fetch and map related Templates
                List<TemplateDetails> templates = templateDetailsRepo.findAllBySurvey_SurveyId(surveyId);
                ArrayList<TemplateResp> templateResps = templates.stream().map(template ->
                        TemplateResp.builder()
                                .templateId(template.getTemplateId())
                                .templateName(template.getTemplateName())
                                .templateType(template.getTemplateType())
                                .surveyId(surveyId)
                                .isActive(template.isActive())
                                .createdBy(template.getCreatedBy())
                                .build()
                ).collect(Collectors.toCollection(ArrayList::new));

                // Fetch and map related Schedules
                List<ScheduleDetails> schedules = scheduleDetailsRepo.findAllBySurvey_SurveyId(surveyId);
                ArrayList<ScheduleResp> scheduleResps = schedules.stream().map(schedule ->
                        ScheduleResp.builder()
                                .scheduleId(schedule.getScheduleId())
                                .surveyId(surveyId)
                                .scheduleTypeId(schedule.getScheduleType() != null ? schedule.getScheduleType().getScheduleTypeId() : null)
                                .scheduleTypeDescription(schedule.getScheduleType() != null ? schedule.getScheduleType().getDescription() : null)
                                .scheduleAttributesId(schedule.getScheduleAttributes() != null ? schedule.getScheduleAttributes().getScheduleAttributesId() : null)
                                .scheduleAttributesDescription(schedule.getScheduleAttributes() != null ? schedule.getScheduleAttributes().getDescription() : null)
                                .isActive(schedule.isActive())
                                .createdBy(schedule.getCreatedBy())
                                .build()
                ).collect(Collectors.toCollection(ArrayList::new));

                // Fetch and map related Blocks
                List<BlockDetails> blocks = blockDetailsRepo.findAllBySurvey_SurveyId(surveyId);
                ArrayList<BlockResp> blockResps = blocks.stream().map(block ->
                        BlockResp.builder()
                                .blockId(block.getBlockId())
                                .surveyId(surveyId)
                                .scheduleId(block.getSchedule() != null ? block.getSchedule().getScheduleId() : null)
                                .blockHeading(block.getBlockHeading())
                                .blockCode(block.getBlockCode())
                                .active(block.isActive())
                                .createdBy(block.getCreatedBy())
                                .build()
                ).collect(Collectors.toCollection(ArrayList::new));

                // Fetch and map related Questions with Skip Conditions & Validations
                List<QuestionDetails> questions = questionDetailsRepo.findAllBySurvey_SurveyId(surveyId);
                ArrayList<QuestionResp> questionResps = questions.stream().map(question -> {
                    List<SkipConditionResp> skipConditions = question.getSkipConditions() != null ?
                            question.getSkipConditions().stream().map(cond -> SkipConditionResp.builder()
                                    .skipConditionId(cond.getSkipConditionId())
                                    .questionId(cond.getTargetQuestion() != null ? cond.getTargetQuestion().getQuestionId() : null)
                                    .condition(cond.getCondition())
                                    .value(cond.getValue())
                                    .build()).collect(Collectors.toList()) : Collections.emptyList();

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
                            .questionCode(question.getQuestionCode())
                            .surveyId(surveyId)
                            .blockId(question.getBlock() != null ? question.getBlock().getBlockId() : null)
                            .questionNumber(question.getQuestionNumber())
                            .fieldInstructions(question.getFieldInstructions())
                            .questionHeading(question.getQuestionHeading())
                            .dataFormate(question.getDataFormate())
                            .dropDownValue(question.getDropDownValue())
                            .isMandatory(question.isMandatory())
                            .skipConditionReq(skipConditions)
                            .questionValidationReq(validations)
                            .questionCondition(question.getQuestionCondition())
                            .build();
                }).collect(Collectors.toCollection(ArrayList::new));

                // Build aggregated TestSurveyResp object for each survey
                return TestSurveyResp.builder()
                        .surveyId(surveyId)
                        .title(survey.getTitle())
                        .periodicCycle(survey.getPeriodicCycle())
                        .surveyCode(survey.getSurveyCode())
                        .divisionId(survey.getDivision() != null ? survey.getDivision().getDivisionId() : null)
                        .divisionDescription(survey.getDivision() != null ? survey.getDivision().getDescription() : null)
                        .enquiryTypeId(survey.getEnquiryType() != null ? survey.getEnquiryType().getEnquiryTypeId() : null)
                        .enquiryTypeDescription(survey.getEnquiryType() != null ? survey.getEnquiryType().getDescription() : null)
                        .frequencyTypeId(survey.getFrequencyType() != null ? survey.getFrequencyType().getFrequencyTypeId() : null)
                        .frequencyTypeDescription(survey.getFrequencyType() != null ? survey.getFrequencyType().getDescription() : null)
                        .seasonalityFactorId(survey.getSeasonalityFactor() != null ? survey.getSeasonalityFactor().getSeasonalityFactorId() : null)
                        .seasonalityFactorDescription(survey.getSeasonalityFactor() != null ? survey.getSeasonalityFactor().getDescription() : null)
                        .surveyDuration(survey.getSurveyDuration())
                        .ruralSamplingFrame(survey.getRuralSamplingFrame())
                        .urbanSamplingFrame(survey.getUrbanSamplingFrame())
                        .samplingSize(survey.getSamplingSize())
                        .surveyObjective(survey.getSurveyObjective())
                        .remarks(survey.getRemarks())
                        .statusId(survey.getStatus() != null ? survey.getStatus().getStatusId() : null)
                        .statusDescription(survey.getStatus() != null ? survey.getStatus().getDescription() : null)
                        .isActive(survey.isActive())
                        .createdBy(survey.getCreatedBy())
                        .templateResp(templateResps)
                        .scheduleResp(scheduleResps)
                        .blockResp(blockResps)
                        .questionResp(questionResps)
                        .build();
            }).collect(Collectors.toList());

            logger.info("Survey details fetched and paginated successfully using status: {}", request.getStatus());

            return ResponseUtils.createPageableSuccessResponse(
                    responseList,
                    surveyPage.getTotalElements(),
                    surveyPage.getTotalPages(),
                    pageable.getPageNumber() + 1,
                    pageable.getPageSize()
            );

        } catch (SDDException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error while fetching survey details using survey status: {}", request.getStatus(), e);
            throw new SDDException(false, "Unexpected error during survey details retrieval using survey status");
        }
    }

    @Transactional(readOnly = true)
    @Override
    public ApiResponseWithPageable<List<TestSurveyResp>> getSurveyInfo(Long userId, GetSurveyByStatusReq request) {
        logger.info("Fetching paginated survey info using survey status: {}", request.getStatus());

        try {

            if (userId == null) {
                throw new SDDException(false, "User ID must be provided");
            }
            UserInfo users = userInfoRepo.findById(userId)
                    .orElseThrow(() -> new SDDException(false, "Invalid User, User not found for ID: " + userId));

            List<MappingUserRole> userRole =mappingUserRoleRepo.findByUser(users);

            List<String> validStatuses = List.of("Pending", "Approved", "Publish","Rejected","Assigned");

            if (request.getStatus() == null ||
                    validStatuses.stream().noneMatch(s -> s.equalsIgnoreCase(request.getStatus()))) {
                throw new SDDException(false, "Invalid status. Status must be Pending, Approved, Publish ,Assigned, or Rejected");
            }


            Long statusId = switch (request.getStatus().toLowerCase()) {
                case "pending" -> 2L;
                case "approved" -> 3L;
                case "publish" -> 4L;
                case "rejected" -> 5L;
                case "assigned" -> 6L;
                default -> throw new SDDException(false, "Unsupported status mapping");
            };

            Page<SurveyDetails> surveyPage;
            Pageable pageable = helperUtils.getPageable(request.getPage(), request.getLimit());

            if (userRole.getFirst().getRole().getRoleId() ==2 || userRole.getFirst().getRole().getRoleId() ==3){
                surveyPage = surveyDetailsRepo.findByStatus_StatusId(statusId, pageable);
            }else{
                throw new SDDException(false, "User Not Authorized for get survey details for this User: " + userId);
            }

            if (surveyPage.getContent().isEmpty()) {
                throw new SDDException(false, "No survey details found for status and for this User: " + request.getStatus() + ":" + userId);
            }

            // 5. Map each SurveyDetails entity into TestSurveyResp with all related details
            List<TestSurveyResp> responseList = surveyPage.getContent().stream().map(survey -> {
                Long surveyId = survey.getSurveyId();

                // Fetch and map related Templates
                List<TemplateDetails> templates = templateDetailsRepo.findAllBySurvey_SurveyId(surveyId);
                ArrayList<TemplateResp> templateResps = templates.stream().map(template ->
                        TemplateResp.builder()
                                .templateId(template.getTemplateId())
                                .templateName(template.getTemplateName())
                                .templateType(template.getTemplateType())
                                .surveyId(surveyId)
                                .isActive(template.isActive())
                                .createdBy(template.getCreatedBy())
                                .build()
                ).collect(Collectors.toCollection(ArrayList::new));

                // Fetch and map related Schedules
                List<ScheduleDetails> schedules = scheduleDetailsRepo.findAllBySurvey_SurveyId(surveyId);
                ArrayList<ScheduleResp> scheduleResps = schedules.stream().map(schedule ->
                        ScheduleResp.builder()
                                .scheduleId(schedule.getScheduleId())
                                .surveyId(surveyId)
                                .scheduleTypeId(schedule.getScheduleType() != null ? schedule.getScheduleType().getScheduleTypeId() : null)
                                .scheduleTypeDescription(schedule.getScheduleType() != null ? schedule.getScheduleType().getDescription() : null)
                                .scheduleAttributesId(schedule.getScheduleAttributes() != null ? schedule.getScheduleAttributes().getScheduleAttributesId() : null)
                                .scheduleAttributesDescription(schedule.getScheduleAttributes() != null ? schedule.getScheduleAttributes().getDescription() : null)
                                .isActive(schedule.isActive())
                                .createdBy(schedule.getCreatedBy())
                                .build()
                ).collect(Collectors.toCollection(ArrayList::new));

                // Fetch and map related Blocks
                List<BlockDetails> blocks = blockDetailsRepo.findAllBySurvey_SurveyId(surveyId);
                ArrayList<BlockResp> blockResps = blocks.stream().map(block ->
                        BlockResp.builder()
                                .blockId(block.getBlockId())
                                .surveyId(surveyId)
                                .scheduleId(block.getSchedule() != null ? block.getSchedule().getScheduleId() : null)
                                .blockHeading(block.getBlockHeading())
                                .blockCode(block.getBlockCode())
                                .active(block.isActive())
                                .createdBy(block.getCreatedBy())
                                .build()
                ).collect(Collectors.toCollection(ArrayList::new));

                // Fetch and map related Questions with Skip Conditions & Validations
                List<QuestionDetails> questions = questionDetailsRepo.findAllBySurvey_SurveyId(surveyId);
                ArrayList<QuestionResp> questionResps = questions.stream().map(question -> {
                    List<SkipConditionResp> skipConditions = question.getSkipConditions() != null ?
                            question.getSkipConditions().stream().map(cond -> SkipConditionResp.builder()
                                    .skipConditionId(cond.getSkipConditionId())
                                    .questionId(cond.getTargetQuestion() != null ? cond.getTargetQuestion().getQuestionId() : null)
                                    .condition(cond.getCondition())
                                    .value(cond.getValue())
                                    .build()).collect(Collectors.toList()) : Collections.emptyList();

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
                            .questionCode(question.getQuestionCode())
                            .surveyId(surveyId)
                            .blockId(question.getBlock() != null ? question.getBlock().getBlockId() : null)
                            .questionNumber(question.getQuestionNumber())
                            .fieldInstructions(question.getFieldInstructions())
                            .questionHeading(question.getQuestionHeading())
                            .dataFormate(question.getDataFormate())
                            .dropDownValue(question.getDropDownValue())
                            .isMandatory(question.isMandatory())
                            .skipConditionReq(skipConditions)
                            .questionValidationReq(validations)
                            .questionCondition(question.getQuestionCondition())
                            .build();
                }).collect(Collectors.toCollection(ArrayList::new));

                // Build aggregated TestSurveyResp object for each survey
                return TestSurveyResp.builder()
                        .surveyId(surveyId)
                        .title(survey.getTitle())
                        .periodicCycle(survey.getPeriodicCycle())
                        .surveyCode(survey.getSurveyCode())
                        .divisionId(survey.getDivision() != null ? survey.getDivision().getDivisionId() : null)
                        .divisionDescription(survey.getDivision() != null ? survey.getDivision().getDescription() : null)
                        .enquiryTypeId(survey.getEnquiryType() != null ? survey.getEnquiryType().getEnquiryTypeId() : null)
                        .enquiryTypeDescription(survey.getEnquiryType() != null ? survey.getEnquiryType().getDescription() : null)
                        .frequencyTypeId(survey.getFrequencyType() != null ? survey.getFrequencyType().getFrequencyTypeId() : null)
                        .frequencyTypeDescription(survey.getFrequencyType() != null ? survey.getFrequencyType().getDescription() : null)
                        .seasonalityFactorId(survey.getSeasonalityFactor() != null ? survey.getSeasonalityFactor().getSeasonalityFactorId() : null)
                        .seasonalityFactorDescription(survey.getSeasonalityFactor() != null ? survey.getSeasonalityFactor().getDescription() : null)
                        .surveyDuration(survey.getSurveyDuration())
                        .ruralSamplingFrame(survey.getRuralSamplingFrame())
                        .urbanSamplingFrame(survey.getUrbanSamplingFrame())
                        .samplingSize(survey.getSamplingSize())
                        .surveyObjective(survey.getSurveyObjective())
                        .remarks(survey.getRemarks())
                        .statusId(survey.getStatus() != null ? survey.getStatus().getStatusId() : null)
                        .statusDescription(survey.getStatus() != null ? survey.getStatus().getDescription() : null)
                        .isActive(survey.isActive())
                        .createdBy(survey.getCreatedBy())
                        .templateResp(templateResps)
                        .scheduleResp(scheduleResps)
                        .blockResp(blockResps)
                        .questionResp(questionResps)
                        .build();
            }).collect(Collectors.toList());

            logger.info("Survey details fetched and paginated successfully using status: {}", request.getStatus());

            return ResponseUtils.createPageableSuccessResponse(
                    responseList,
                    surveyPage.getTotalElements(),
                    surveyPage.getTotalPages(),
                    pageable.getPageNumber() + 1,
                    pageable.getPageSize()
            );

        } catch (SDDException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error while fetching survey details using survey status: {}", request.getStatus(), e);
            throw new SDDException(false, "Unexpected error during survey details retrieval using survey status");
        }
    }


    //**********************SUBMITTED SURVEY******************************/

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse<DefaultResponse> submittedSurvey(Long userId, TestSurveyReq request) {
        logger.info("Starting survey submission process for surveyId: {}", request != null ? request.getSurveyId() : null);

        try {
            if (request == null || request.getSurveyId() == null) {
                throw new SDDException(false, "Survey ID must be provided");
            }
            if (request.getStatus() == null || !request.getStatus().equalsIgnoreCase("Pending")) {
                throw new SDDException(false, "Invalid status. Status must be 'Pending'");
            }
            SurveyDetails survey = surveyDetailsRepo.findById(request.getSurveyId())
                    .orElseThrow(() -> new SDDException(false, "Survey not found for ID: " + request.getSurveyId()));
            MasStatus pendingStatus = masStatusRepo.findById(2L)
                    .orElseThrow(() -> new SDDException(false, "Status configuration not found for ID: 2"));
            survey.setStatus(pendingStatus);
            survey.setUpdatedBy(userId);
            surveyDetailsRepo.save(survey);
            logger.info("Survey submitted successfully for surveyId: {}", request.getSurveyId());
            DefaultResponse defaultResponse = new DefaultResponse();
            defaultResponse.setMsg("Your survey is successfully submitted, pending for approval");
            return ResponseUtils.createSuccessResponse(defaultResponse, new TypeReference<DefaultResponse>() {});

        } catch (SDDException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error while submitting survey", e);
            throw new SDDException(false, "Unexpected error during survey submission");
        }
    }



    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse<DefaultResponse> surveyAction(Long userId, SurveyActionReq request) {
        logger.info("Starting survey action process for surveyId: {} by user: {}",
                request != null ? request.getSurveyId() : null, userId);

        try {
            if (request == null || request.getSurveyId() == null) {
                throw new SDDException(false, "Survey ID must be provided");
            }

            if (request.getAction() == null) {
                throw new SDDException(false, "Action must be provided");
            }

            SurveyDetails survey = surveyDetailsRepo.findById(request.getSurveyId())
                    .orElseThrow(() -> new SDDException(false, "Survey not found for ID: " + request.getSurveyId()));

            MasStatus targetStatus;

            if (request.getAction().equalsIgnoreCase("approved")) {
                targetStatus = masStatusRepo.findById(3L)
                        .orElseThrow(() -> new SDDException(false, "Approved status configuration not found (ID: 3)"));
                // --- TRIGGER DYNAMIC TABLE CREATION UPON APPROVAL ---
                helperUtils.generateResponseTableForSurvey(survey.getSurveyId(), survey.getSurveyCode());

            } else if (request.getAction().equalsIgnoreCase("rejected")) {
                targetStatus = masStatusRepo.findById(5L)
                        .orElseThrow(() -> new SDDException(false, "Rejected status configuration not found (ID: 5)"));
            } else {
                throw new SDDException(false, "Invalid action. Action must be 'approved' or 'rejected'");
            }
            survey.setStatus(targetStatus);
            survey.setApprovedBy(userId);
            survey.setApprovedAt(OffsetDateTime.now());
            survey.setUpdatedBy(userId);
            surveyDetailsRepo.save(survey);
            logger.info("Survey action '{}' completed successfully for surveyId: {}",
                    targetStatus.getDescription(), request.getSurveyId());
            DefaultResponse defaultResponse = new DefaultResponse();
            defaultResponse.setMsg("Survey " + targetStatus.getDescription().toLowerCase() + " successfully");
            return ResponseUtils.createSuccessResponse(defaultResponse, new TypeReference<DefaultResponse>() {});

        } catch (SDDException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error while processing survey action for surveyId: {}",
                    request != null ? request.getSurveyId() : null, e);
            throw new SDDException(false, "Unexpected error during survey action processing");
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse<DefaultResponse> surveyAssign(Long userId, SurveyAssignReq request) {
        logger.info("Starting survey assignment process for surveyId: {} to assignUserId: {} by user: {}",
                request != null ? request.getSurveyId() : null,
                request != null ? request.getAssignUserId() : null,
                userId);

        try {
            if (request == null || request.getSurveyId() == null || request.getAssignUserId() == null) {
                throw new SDDException(false, "Survey ID and Assign User ID must be provided");
            }

            SurveyDetails survey = surveyDetailsRepo.findById(request.getSurveyId())
                    .orElseThrow(() -> new SDDException(false, "Survey not found for ID: " + request.getSurveyId()));

            MasStatus assignedStatus = masStatusRepo.findById(6L)
                    .orElseThrow(() -> new SDDException(false, "Status configuration not found for ID: 6 (Assigned)"));

            AssignDetails assignDetails = AssignDetails.builder()
                    .survey(survey)
                    .assignTo(request.getAssignUserId())
                    .isActive(true)
                    .createdBy(userId)
                    .build();

            assignDetailsRepo.save(assignDetails);

            survey.setStatus(assignedStatus);
            survey.setUpdatedBy(userId);
            surveyDetailsRepo.save(survey);

            logger.info("Survey assigned successfully for surveyId: {} to userId: {}", request.getSurveyId(), request.getAssignUserId());

            DefaultResponse defaultResponse = new DefaultResponse();
            defaultResponse.setMsg("Survey assigned successfully");

            return ResponseUtils.createSuccessResponse(defaultResponse, new TypeReference<DefaultResponse>() {});

        } catch (SDDException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error while assigning survey for surveyId: {}",
                    request != null ? request.getSurveyId() : null, e);
            throw new SDDException(false, "Unexpected error during survey assignment");
        }
    }



    @Override
    public ApiResponseWithPageable<List<TestSurveyResp>> getSurveyInfoFieldUser(Long userId, GetSurveyByStatusReq request) {
        logger.info("Fetching paginated survey info for field User: {}", userId);

        try {
            if (userId == null) {
                throw new SDDException(false, "User ID must be provided");
            }

            UserInfo users = userInfoRepo.findById(userId)
                    .orElseThrow(() -> new SDDException(false, "Invalid User, User not found for ID: " + userId));

            // 1. Check if the user has Role ID 7
            boolean hasFieldUserRole = mappingUserRoleRepo.findByUser(users).stream()
                    .anyMatch(mapping -> mapping.getRole() != null && mapping.getRole().getRoleId() != null
                            && mapping.getRole().getRoleId().equals(7L));

            if (!hasFieldUserRole) {
                throw new SDDException(false, "Access denied. User does not have the required field user role (Role ID: 7)");
            }

            // 2. Fetch assigned survey IDs from AssignDetails table where assignTo = userId and isActive = true
            List<AssignDetails> assignDetailsList = assignDetailsRepo.findByAssignToAndIsActive(userId, true);

            List<Long> assignedSurveyIds = assignDetailsList.stream()
                    .filter(assign -> assign.getSurvey() != null)
                    .map(assign -> assign.getSurvey().getSurveyId())
                    .distinct()
                    .collect(Collectors.toList());

            Pageable pageable = helperUtils.getPageable(request.getPage(), request.getLimit());
            Page<SurveyDetails> surveyPage;

            if (assignedSurveyIds.isEmpty()) {
                // If no surveys are assigned, return empty page or throw exception
                throw new SDDException(false, "No assigned surveys found for this user");
            }

            // 3. Find surveys from the assigned list that have Status ID 6 (Assigned) with pagination
            // Note: You need to add this method in your SurveyDetailsRepo
            surveyPage = surveyDetailsRepo.findBySurveyIdInAndStatus_StatusId(assignedSurveyIds, 6L, pageable);

            if (surveyPage.getContent().isEmpty()) {
                throw new SDDException(false, "No survey details found with status 'Assigned' (ID: 6) for this User: " + userId);
            }

            // 4. Map each SurveyDetails entity into TestSurveyResp with all related details
            List<TestSurveyResp> responseList = surveyPage.getContent().stream().map(survey -> {
                Long surveyId = survey.getSurveyId();

                // Fetch and map related Templates
                List<TemplateDetails> templates = templateDetailsRepo.findAllBySurvey_SurveyId(surveyId);
                ArrayList<TemplateResp> templateResps = templates.stream().map(template ->
                        TemplateResp.builder()
                                .templateId(template.getTemplateId())
                                .templateName(template.getTemplateName())
                                .templateType(template.getTemplateType())
                                .surveyId(surveyId)
                                .isActive(template.isActive())
                                .createdBy(template.getCreatedBy())
                                .build()
                ).collect(Collectors.toCollection(ArrayList::new));

                // Fetch and map related Schedules
                List<ScheduleDetails> schedules = scheduleDetailsRepo.findAllBySurvey_SurveyId(surveyId);
                ArrayList<ScheduleResp> scheduleResps = schedules.stream().map(schedule ->
                        ScheduleResp.builder()
                                .scheduleId(schedule.getScheduleId())
                                .surveyId(surveyId)
                                .scheduleTypeId(schedule.getScheduleType() != null ? schedule.getScheduleType().getScheduleTypeId() : null)
                                .scheduleTypeDescription(schedule.getScheduleType() != null ? schedule.getScheduleType().getDescription() : null)
                                .scheduleAttributesId(schedule.getScheduleAttributes() != null ? schedule.getScheduleAttributes().getScheduleAttributesId() : null)
                                .scheduleAttributesDescription(schedule.getScheduleAttributes() != null ? schedule.getScheduleAttributes().getDescription() : null)
                                .isActive(schedule.isActive())
                                .createdBy(schedule.getCreatedBy())
                                .build()
                ).collect(Collectors.toCollection(ArrayList::new));

                // Fetch and map related Blocks
                List<BlockDetails> blocks = blockDetailsRepo.findAllBySurvey_SurveyId(surveyId);
                ArrayList<BlockResp> blockResps = blocks.stream().map(block ->
                        BlockResp.builder()
                                .blockId(block.getBlockId())
                                .surveyId(surveyId)
                                .scheduleId(block.getSchedule() != null ? block.getSchedule().getScheduleId() : null)
                                .blockHeading(block.getBlockHeading())
                                .blockCode(block.getBlockCode())
                                .active(block.isActive())
                                .createdBy(block.getCreatedBy())
                                .build()
                ).collect(Collectors.toCollection(ArrayList::new));

                // Fetch and map related Questions with Skip Conditions & Validations
                List<QuestionDetails> questions = questionDetailsRepo.findAllBySurvey_SurveyId(surveyId);
                ArrayList<QuestionResp> questionResps = questions.stream().map(question -> {
                    List<SkipConditionResp> skipConditions = question.getSkipConditions() != null ?
                            question.getSkipConditions().stream().map(cond -> SkipConditionResp.builder()
                                    .skipConditionId(cond.getSkipConditionId())
                                    .questionId(cond.getTargetQuestion() != null ? cond.getTargetQuestion().getQuestionId() : null)
                                    .condition(cond.getCondition())
                                    .value(cond.getValue())
                                    .build()).collect(Collectors.toList()) : Collections.emptyList();

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
                            .questionCode(question.getQuestionCode())
                            .surveyId(surveyId)
                            .blockId(question.getBlock() != null ? question.getBlock().getBlockId() : null)
                            .questionNumber(question.getQuestionNumber())
                            .fieldInstructions(question.getFieldInstructions())
                            .questionHeading(question.getQuestionHeading())
                            .dataFormate(question.getDataFormate())
                            .dropDownValue(question.getDropDownValue())
                            .isMandatory(question.isMandatory())
                            .skipConditionReq(skipConditions)
                            .questionValidationReq(validations)
                            .questionCondition(question.getQuestionCondition())
                            .build();
                }).collect(Collectors.toCollection(ArrayList::new));

                // Build aggregated TestSurveyResp object for each survey
                return TestSurveyResp.builder()
                        .surveyId(surveyId)
                        .title(survey.getTitle())
                        .periodicCycle(survey.getPeriodicCycle())
                        .surveyCode(survey.getSurveyCode())
                        .divisionId(survey.getDivision() != null ? survey.getDivision().getDivisionId() : null)
                        .divisionDescription(survey.getDivision() != null ? survey.getDivision().getDescription() : null)
                        .enquiryTypeId(survey.getEnquiryType() != null ? survey.getEnquiryType().getEnquiryTypeId() : null)
                        .enquiryTypeDescription(survey.getEnquiryType() != null ? survey.getEnquiryType().getDescription() : null)
                        .frequencyTypeId(survey.getFrequencyType() != null ? survey.getFrequencyType().getFrequencyTypeId() : null)
                        .frequencyTypeDescription(survey.getFrequencyType() != null ? survey.getFrequencyType().getDescription() : null)
                        .seasonalityFactorId(survey.getSeasonalityFactor() != null ? survey.getSeasonalityFactor().getSeasonalityFactorId() : null)
                        .seasonalityFactorDescription(survey.getSeasonalityFactor() != null ? survey.getSeasonalityFactor().getDescription() : null)
                        .surveyDuration(survey.getSurveyDuration())
                        .ruralSamplingFrame(survey.getRuralSamplingFrame())
                        .urbanSamplingFrame(survey.getUrbanSamplingFrame())
                        .samplingSize(survey.getSamplingSize())
                        .surveyObjective(survey.getSurveyObjective())
                        .remarks(survey.getRemarks())
                        .statusId(survey.getStatus() != null ? survey.getStatus().getStatusId() : null)
                        .statusDescription(survey.getStatus() != null ? survey.getStatus().getDescription() : null)
                        .isActive(survey.isActive())
                        .createdBy(survey.getCreatedBy())
                        .templateResp(templateResps)
                        .scheduleResp(scheduleResps)
                        .blockResp(blockResps)
                        .questionResp(questionResps)
                        .build();
            }).collect(Collectors.toList());

            logger.info("Survey details fetched and paginated successfully For Field user: {}", userId);

            return ResponseUtils.createPageableSuccessResponse(
                    responseList,
                    surveyPage.getTotalElements(),
                    surveyPage.getTotalPages(),
                    pageable.getPageNumber() + 1,
                    pageable.getPageSize()
            );

        } catch (SDDException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error while fetching survey details for Field User: {}", userId, e);
            throw new SDDException(false, "Unexpected error during survey details For Field user");
        }
    }


    //**********************DATA COLLECTION******************************/


    @Transactional(rollbackFor = Exception.class)
    @Override
    public ApiResponse<DefaultResponse> saveDataCollection(Long userId, SurveyDataCollectionReq request) {
        logger.info("Starting survey data collection process for surveyId: {} by user: {}",
                request != null ? request.getSurveyId() : null, userId);

        try {
            // 1. Validate request parameters
            if (request == null || request.getSurveyId() == null || request.getAnswers() == null || request.getAnswers().isEmpty()) {
                throw new SDDException(false, "Survey ID and answers must be provided");
            }

            if (userId == null) {
                throw new SDDException(false, "User ID must be provided");
            }

            // 2. Fetch SurveyDetails to ensure the survey exists and get its surveyCode
            SurveyDetails survey = surveyDetailsRepo.findById(request.getSurveyId())
                    .orElseThrow(() -> new SDDException(false, "Survey not found for ID: " + request.getSurveyId()));

            String rawSurveyCode = survey.getSurveyCode();
            if (rawSurveyCode == null || rawSurveyCode.trim().isEmpty()) {
                throw new SDDException(false, "Survey code is missing for survey ID: " + request.getSurveyId());
            }

            // 3. Clean and format table name (e.g., fact_plfs2026)
            String cleanedSurveyCode = rawSurveyCode.replaceAll("\\s+", "").replaceAll("[^a-zA-Z0-9_]", "").toLowerCase();
            String tableName = "fact_" + cleanedSurveyCode;

            // 4. Build dynamic SQL INSERT query safely using JdbcTemplate
            StringBuilder columns = new StringBuilder("user_id");
            StringBuilder placeholders = new StringBuilder("?");
            List<Object> values = new ArrayList<>();
            values.add(userId);

            for (Map.Entry<String, String> entry : request.getAnswers().entrySet()) {
                // Sanitize questionCode (column name) to be 100% safe against SQL injection
                String colName = entry.getKey().replaceAll("\\s+", "").replaceAll("[^a-zA-Z0-9_]", "").toLowerCase();

                columns.append(", ").append(colName);
                placeholders.append(", ?");
                values.add(entry.getValue());
            }

            String sql = String.format("INSERT INTO %s (%s) VALUES (%s)", tableName, columns.toString(), placeholders.toString());

            // 5. Execute insertion via JdbcTemplate
            jdbcTemplate.update(sql, values.toArray());

            logger.info("Survey data collection saved successfully into table '{}' for user: {}", tableName, userId);

            // 6. Prepare success response
            DefaultResponse defaultResponse = new DefaultResponse();
            defaultResponse.setMsg("Survey data collected and saved successfully");

            return ResponseUtils.createSuccessResponse(defaultResponse, new TypeReference<DefaultResponse>() {});

        } catch (SDDException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error while saving survey data collection for surveyId: {}",
                    request != null ? request.getSurveyId() : null, e);
            throw new SDDException(false, "Unexpected error during survey data collection submission");
        }
    }

//    @Override
//    public ApiResponseWithPageable<List<Map<String, Object>>> getCollectedData(GetSurveyDataReq request) {
//        logger.info("Fetching paginated data for surveyCode: {} or surveyId: {}",
//                request != null ? request.getSurveyCode() : null,
//                request != null ? request.getSurveyId() : null);
//        try {
//            // 1. Validation
//            if (request == null || (request.getSurveyId() == null && (request.getSurveyCode() == null || request.getSurveyCode().trim().isEmpty()))) {
//                throw new SDDException(false, "Either Survey ID or Survey Code must be provided");
//            }
//            String surveyCode = request.getSurveyCode();
//            // Agar request mein sirf surveyId diya hai, toh database se surveyCode nikal lein
//            if ((surveyCode == null || surveyCode.trim().isEmpty()) && request.getSurveyId() != null) {
//                SurveyDetails survey = surveyDetailsRepo.findById(request.getSurveyId())
//                        .orElseThrow(() -> new SDDException(false, "Survey not found for ID: " + request.getSurveyId()));
//                surveyCode = survey.getSurveyCode();
//            }
//            // 2. Clean and format table name (e.g., fact_plfs2026)
//            String cleanedSurveyCode = surveyCode.replaceAll("\\s+", "").replaceAll("[^a-zA-Z0-9_]", "").toLowerCase();
//            String tableName = "fact_" + cleanedSurveyCode;
//            // 3. Get pageable instance (Default page: 1, limit: 10 ya jo bhi aapka helper utils provide kare)
//            Pageable pageable = helperUtils.getPageable(request.getPage(), request.getLimit());
//            int limit = pageable.getPageSize();
//            long offset = pageable.getOffset();
//            List<Map<String, Object>> resultList;
//            long totalElements = 0;
//            try {
//                // 4. Count total records in the dynamic table for pagination metadata
//                String countSql = String.format("SELECT COUNT(*) FROM %s", tableName);
//                totalElements = jdbcTemplate.queryForObject(countSql, Long.class);
//
//                // 5. Dynamic Paginated Query (SELECT * FROM table LIMIT ? OFFSET ?)
//                String sql = String.format("SELECT * FROM %s LIMIT ? OFFSET ?", tableName);
//                resultList = jdbcTemplate.queryForList(sql, limit, offset);
//
//            } catch (Exception e) {
//                logger.error("Table '{}' does not exist or failed to query data", tableName, e);
//                throw new SDDException(false, "Response table for survey code '" + surveyCode + "' does not exist yet.");
//            }
//
//            // Calculate total pages
//            int totalPages = (limit > 0) ? (int) Math.ceil((double) totalElements / limit) : 0;
//
//            logger.info("Successfully fetched {} records from table '{}'", resultList.size(), tableName);
//
//            // 6. Return pageable success response matching your controller signature
//            return ResponseUtils.createPageableSuccessResponse(
//                    resultList,
//                    totalElements,
//                    totalPages,
//                    pageable.getPageNumber() + 1,
//                    pageable.getPageSize()
//            );
//
//        } catch (SDDException e) {
//            throw e;
//        } catch (Exception e) {
//            logger.error("Error while fetching survey data collection", e);
//            throw new SDDException(false, "Unexpected error during fetching survey data");
//        }
//    }


    @Override
    public ApiResponseWithPageable<List<Map<String, Object>>> getCollectedData(GetSurveyDataReq request) {
        logger.info("Fetching paginated data with question headings for surveyCode: {} or surveyId: {}",
                request != null ? request.getSurveyCode() : null,
                request != null ? request.getSurveyId() : null);

        try {
            // 1. Validation
            if (request == null || (request.getSurveyId() == null && (request.getSurveyCode() == null || request.getSurveyCode().trim().isEmpty()))) {
                throw new SDDException(false, "Either Survey ID or Survey Code must be provided");
            }

            Long surveyId = request.getSurveyId();
            String surveyCode = request.getSurveyCode();

            // 2. Fetch survey details to get surveyId and surveyCode if either is missing
            Long finalSurveyId = request.getSurveyId();
            String resolvedSurveyCode = request.getSurveyCode();

            if (finalSurveyId != null && (resolvedSurveyCode == null || resolvedSurveyCode.trim().isEmpty())) {
                SurveyDetails survey = surveyDetailsRepo.findById(finalSurveyId)
                        .orElseThrow(() -> new SDDException(false, "Survey not found for ID: " + finalSurveyId));
                resolvedSurveyCode = survey.getSurveyCode();

            }

            // 3. Fetch all Question Details for this survey to build a Code -> Heading lookup map
            List<QuestionDetails> questions = questionDetailsRepo.findAllBySurvey_SurveyId(surveyId);

            // Map banayenge: Key = questionCode (lowercase/clean), Value = questionHeading
            Map<String, String> questionHeadingMap = new HashMap<>();
            for (QuestionDetails q : questions) {
                if (q.getQuestionCode() != null) {
                    String cleanCode = q.getQuestionCode().replaceAll("\\s+", "").replaceAll("[^a-zA-Z0-9_]", "").toLowerCase();
                    questionHeadingMap.put(cleanCode, q.getQuestionHeading());
                }
            }

            // 4. Clean and format table name (e.g., fact_plfs2026)
            String cleanedSurveyCode = surveyCode.replaceAll("\\s+", "").replaceAll("[^a-zA-Z0-9_]", "").toLowerCase();
            String tableName = "fact_" + cleanedSurveyCode;

            // 5. Get pageable instance
            Pageable pageable = helperUtils.getPageable(request.getPage(), request.getLimit());
            int limit = pageable.getPageSize();
            long offset = pageable.getOffset();

            List<Map<String, Object>> rawResultList;
            long totalElements = 0;

            try {
                // Count total records
                String countSql = String.format("SELECT COUNT(*) FROM %s", tableName);
                totalElements = jdbcTemplate.queryForObject(countSql, Long.class);

                // Fetch paginated data from dynamic table
                String sql = String.format("SELECT * FROM %s LIMIT ? OFFSET ?", tableName);
                rawResultList = jdbcTemplate.queryForList(sql, limit, offset);

            } catch (Exception e) {
                logger.error("Table '{}' does not exist or failed to query data", tableName, e);
                throw new SDDException(false, "Response table for survey code '" + surveyCode + "' does not exist yet.");
            }

            // 6. Transform raw data to include Question Headings alongside Question Codes
            List<Map<String, Object>> enhancedResultList = new ArrayList<>();

            for (Map<String, Object> row : rawResultList) {
                Map<String, Object> enhancedRow = new LinkedHashMap<>(); // LinkedHashMap preserves order

                for (Map.Entry<String, Object> entry : row.entrySet()) {
                    String columnName = entry.getKey();
                    Object value = entry.getValue();

                    // System columns (jaise response_id, user_id, submitted_at) ko waise hi rakhne dein
                    if (columnName.equals("response_id") || columnName.equals("user_id") || columnName.equals("submitted_at")) {
                        enhancedRow.put(columnName, value);
                    } else {
                        // Question code ke liye heading fetch karein
                        String heading = questionHeadingMap.get(columnName);

                        // Ek structured format ya nested object bana sakte hain, ya phir clear naming de sakte hain
                        Map<String, Object> questionMeta = new LinkedHashMap<>();
                        questionMeta.put("questionCode", columnName);
                        questionMeta.put("questionHeading", heading != null ? heading : "Unknown Heading");
                        questionMeta.put("answer", value);

                        enhancedRow.put(columnName, questionMeta);
                    }
                }
                enhancedResultList.add(enhancedRow);
            }

            // Calculate total pages
            int totalPages = (limit > 0) ? (int) Math.ceil((double) totalElements / limit) : 0;

            logger.info("Successfully fetched and enhanced {} records from table '{}'", enhancedResultList.size(), tableName);

            return ResponseUtils.createPageableSuccessResponse(
                    enhancedResultList,
                    totalElements,
                    totalPages,
                    pageable.getPageNumber() + 1,
                    pageable.getPageSize()
            );

        } catch (SDDException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error while fetching enhanced survey data collection", e);
            throw new SDDException(false, "Unexpected error during fetching survey data");
        }
    }
}
