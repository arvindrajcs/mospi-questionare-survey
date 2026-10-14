package com.mospi.questionare_survey.jwt;

import com.mospi.questionare_survey.entity.UserInfo;
import com.mospi.questionare_survey.repository.MappingUserRoleRepo;
import com.mospi.questionare_survey.repository.UserInfoRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserInfoDetailsService implements UserDetailsService {

    private final UserInfoRepo repo;
    private final MappingUserRoleRepo mappingUserRoleRepo;

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        UserInfo user = repo.findByUsernameIgnoreCase(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found"));

        String password = user.getPasswordHash();

        // Database se user ke saare roles fetch karein
        // (Assume mappingUserRoleRepo me ek method hai jo user_id ke basis par role names deta hai)
        List<String> roles = mappingUserRoleRepo.findRoleNamesByUserId(user.getUserId());

        // Default active role pehla role set kar sakte hain
        String defaultActiveRole = roles.isEmpty() ? null : roles.get(0);

        return new UserInfoPrincipal(username, roles, defaultActiveRole, password);
    }
}