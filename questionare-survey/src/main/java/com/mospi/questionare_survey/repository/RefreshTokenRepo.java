package com.mospi.questionare_survey.repository;


import com.mospi.questionare_survey.entity.RefreshToken;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RefreshTokenRepo extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByTokenHash(String tokenHash);
    List<RefreshToken> findByUserIdAndRevokedFalse(Long userId);

    Page<RefreshToken> findByUserIdAndRevokedFalse(Long userId, Pageable pageable);



    Long countDistinctByRevokedFalse();

    List<RefreshToken> findByRevokedFalse();

    void deleteByUserId(Long userId); // to revoke all
}
