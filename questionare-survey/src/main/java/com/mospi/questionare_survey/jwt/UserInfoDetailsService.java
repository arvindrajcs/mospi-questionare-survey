package com.mospi.questionare_survey.jwt;


import com.mospi.questionare_survey.entity.UserInfo;
import com.mospi.questionare_survey.repository.MappingUserRoleRepo;
import com.mospi.questionare_survey.repository.UserInfoRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

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
        String roleName = null;
        String password = user.getPasswordHash();

        return new UserInfoPrincipal(username, roleName, password);
    }
}

