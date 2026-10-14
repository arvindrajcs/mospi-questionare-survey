package com.mospi.questionare_survey.jwt;

import com.mospi.questionare_survey.entity.UserInfo;
import com.mospi.questionare_survey.repository.UserInfoRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
@Service
public class CustomeUserDetailsImpl implements UserDetailsService {

    @Autowired
    private UserInfoRepo userInfoRepo;

    @Override
    public UserInfo loadUserByUsername(String username) throws UsernameNotFoundException {

        UserInfo user =userInfoRepo.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User Not found !!");
        }
        return user;
    }

}
