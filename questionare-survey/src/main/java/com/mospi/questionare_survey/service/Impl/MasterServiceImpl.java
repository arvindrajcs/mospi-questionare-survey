package com.mospi.questionare_survey.service.Impl;


import com.fasterxml.jackson.core.type.TypeReference;
import com.mospi.questionare_survey.entity.MasMenu;
import com.mospi.questionare_survey.exception.SDDException;
import com.mospi.questionare_survey.helperUtil.ErrorCode;
import com.mospi.questionare_survey.helperUtil.HelperUtils;
import com.mospi.questionare_survey.helperUtil.ResponseUtils;
import com.mospi.questionare_survey.repository.MasMenuRepo;
import com.mospi.questionare_survey.request.CreateMenuReq;
import com.mospi.questionare_survey.request.GetMenuReq;
import com.mospi.questionare_survey.request.MenuStatusReq;
import com.mospi.questionare_survey.response.ApiResponse;
import com.mospi.questionare_survey.response.ApiResponseWithPageable;
import com.mospi.questionare_survey.response.DefaultResponse;
import com.mospi.questionare_survey.service.MasterService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.print.Pageable;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MasterServiceImpl implements MasterService {

    private final Logger logger = LoggerFactory.getLogger(MasterServiceImpl.class);

    @Autowired
    private HelperUtils helperUtill;
    @Autowired
    private MasMenuRepo masMenuRepo;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<DefaultResponse> createMenu(Long userId, CreateMenuReq request) {
        try {
            MasMenu menu = MasMenu.builder()
                    .parentId(request.getParentId())
                    .childId(request.getChildId())
                    .menuUrl(request.getMenuUrl())
                    .iconName(request.getIconName())
                    .labelName(request.getLabelName())
                    .menuType(request.getMenuType())
                    .menuOrder(request.getMenuOrder() != null ? request.getMenuOrder() : 1)
                    .isActive(true)
                    .createdBy(userId)
                    .updatedBy(userId)
                    .build();
            masMenuRepo.save(menu);
            logger.info("Menu created successfully by userId: {}", userId);
            DefaultResponse response = new DefaultResponse();
            response.setMsg("Menu created successfully");
            return ResponseUtils.createSuccessResponse(response);
        } catch (SDDException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error while creating menu for userId: {}", userId, e);
            throw new SDDException(false, "Unexpected error during menu creation");
        }
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<DefaultResponse> updateMenu(Long userId, CreateMenuReq request) {
        try {
            // 1. Fetch existing menu or throw custom exception if not found
            MasMenu existingMenu = masMenuRepo.findById(request.getMenuId())
                    .orElseThrow(() -> new SDDException(false, ErrorCode.AU1001));

            // 2. Update fields from the request DTO
            existingMenu.setParentId(request.getParentId());
            existingMenu.setChildId(request.getChildId());
            existingMenu.setMenuUrl(request.getMenuUrl());
            existingMenu.setIconName(request.getIconName());
            existingMenu.setLabelName(request.getLabelName());
            existingMenu.setMenuType(request.getMenuType());
            if (request.getMenuOrder() != null) {
                existingMenu.setMenuOrder(request.getMenuOrder());
            }
            existingMenu.setUpdatedBy(userId);
            masMenuRepo.save(existingMenu);
            DefaultResponse defaultResponse = new DefaultResponse("Menu updated successfully");
            return ResponseUtils.createSuccessResponse(defaultResponse);

        } catch (SDDException e) {
            throw e;
        } catch (Exception e) {
            throw new SDDException(false, ErrorCode.AU1000);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<DefaultResponse> updateMenuStatus(Long userId, MenuStatusReq request) {
        try {

            MasMenu existingMenu = masMenuRepo.findById(request.getMenuId())
                    .orElseThrow(() -> new SDDException(false, ErrorCode.AU1001));

            existingMenu.setActive(request.isActive());
            existingMenu.setUpdatedBy(userId);
            masMenuRepo.save(existingMenu);

            String message = request.isActive() ? "Menu activated successfully" : "Menu deactivated successfully";
            DefaultResponse defaultResponse = new DefaultResponse(message);
            return ResponseUtils.createSuccessResponse(defaultResponse);

        } catch (SDDException e) {
            throw e;
        } catch (Exception e) {
            throw new SDDException(false, ErrorCode.AU1000);
        }
    }

    @Override
    public ApiResponse<MasMenu> getMenuByMenuId(GetMenuReq request) {
        try {

            MasMenu menu = masMenuRepo.findById(request.getMenuId())
                    .orElseThrow(() -> new SDDException(false, ErrorCode.AU1001));

            return ResponseUtils.createSuccessResponse(
                    menu, new TypeReference<MasMenu>() {});

        } catch (SDDException e) {
            throw e;
        } catch (Exception e) {
            throw new SDDException(false, ErrorCode.AU1000);
        }
    }


//    @Override
//    public ApiResponseWithPageable<List<MasMenu>> getAllMenus(GetMenuReq request) {
//        try {
//            int page = (request.getPage() != null && request.getPage() >= 0) ? request.getPage().intValue() : 0;
//            int limit = (request.getLimit() != null && request.getLimit() > 0) ? request.getLimit().intValue() : 10;
//
//            org.springframework.data.domain.Pageable pageable =
//                    org.springframework.data.domain.PageRequest.of(page, limit, org.springframework.data.domain.Sort.by("menuOrder").ascending());
//
//            org.springframework.data.domain.Page<MasMenu> menuPage = masMenuRepo.findAll(pageable);
//
//            List<MasMenu> menus = menuPage.getContent();
//            Long totalElements = menuPage.getTotalElements();
//            int totalPages = menuPage.getTotalPages();
//            int currentPage = menuPage.getNumber();
//            int pageSize = menuPage.getSize();
//
//            return new ApiResponseWithPageable<>(
//                    menus,
//                    true,
//                    "Menus fetched successfully",
//                    totalElements,
//                    totalPages,
//                    currentPage,
//                    pageSize
//            );
//
//        } catch (SDDException e) {
//            throw e;
//        } catch (Exception e) {
//            throw new SDDException(false, ErrorCode.AU1000);
//        }
//    }


    @Override
    public ApiResponseWithPageable<List<MasMenu>> getAllMenus(GetMenuReq request) {

        logger.info("Fetching Menus. Page: {}, Limit: {}", request.getPage(), request.getLimit());

        try {

            Pageable pageable =
                    helperUtill.getPageable(
                            request.getPage(),
                            request.getLimit()
                    );

            Page<MasMenu> menusPage;

            if (request.getLabel() != null &&
                    !request.getLabel().trim().isEmpty()) {

                menusPage =
                        menuRepository
                                .findByLabelContainingIgnoreCase(
                                        request.getLabel(),
                                        pageable
                                );

            } else {

                // Fetch all menus
                menusPage =
                        menuRepository.findAll(pageable);
            }

            return ResponseUtils.createPageableSuccessResponse(
                    menusPage.getContent(),
                    menusPage.getTotalElements(),
                    menusPage.getTotalPages(),
                    pageable.getPageNumber() + 1,
                    pageable.getPageSize()
            );

        }

        catch (SDDException e) {
            throw e;
        }

        catch (Exception e) {

            logger.error("Error while fetching Menu", e);

            throw new SDDException(
                    false,
                    "Unexpected error while fetching Menu"
            );
        }
    }

}
