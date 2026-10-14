package com.mospi.questionare_survey.jwt;

import com.rshaa.Auth.entity.AppUser;
import com.rshaa.Auth.repository.UserDetailsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AppUserDetailsService implements UserDetailsService {

    private final UserDetailsRepository repo;

    @Override
    public UserDetails loadUserByUsername(String loginId)
            throws UsernameNotFoundException {

        AppUser user = repo.findByLoginIdIgnoreCase(loginId)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found"));

        String username = user.getLoginId();
        String roleName = user.getRole().getDescription();
        String password = user.getPassword();

        return new AppUserPrincipal(username, roleName, password);
    }
}

