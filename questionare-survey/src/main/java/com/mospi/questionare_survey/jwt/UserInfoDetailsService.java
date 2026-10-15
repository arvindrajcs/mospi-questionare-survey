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

        UserInfo user = repo.findByUsernameIgnoreCaseAndIsActive(username, true);

        if (user == null) {
            throw new UsernameNotFoundException("User not found or inactive");
        }

        // 2. Get the pre-encrypted password hash from database
        String passwordHash = user.getPasswordHash();

        // 3. Fetch all assigned roles for this user
        List<String> roles = mappingUserRoleRepo.findRoleNamesByUserId(user.getUserId());

        // 4. Set default active role (first role in the list)
        String defaultActiveRole = roles.isEmpty() ? null : roles.get(0);

        // 5. Return Principal containing passwordHash so DaoAuthenticationProvider can verify it using BCryptPasswordEncoder
        return new UserInfoPrincipal(username, roles, defaultActiveRole, passwordHash);
    }
}