package com.mospi.questionare_survey.repository;


import com.mospi.questionare_survey.entity.MasEnquiryType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MasEnquiryTypeRepo extends JpaRepository<MasEnquiryType, Long> {

}
