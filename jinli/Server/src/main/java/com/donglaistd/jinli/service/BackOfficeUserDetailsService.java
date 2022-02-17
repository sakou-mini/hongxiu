package com.donglaistd.jinli.service;

import com.donglaistd.jinli.database.dao.BackOfficeUserRepository;
import com.donglaistd.jinli.database.entity.backoffice.BackOfficeUser;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class BackOfficeUserDetailsService implements UserDetailsService {

    @Autowired
    private BackOfficeUserRepository backOfficeUserRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if (StringUtils.isBlank(username)) {
            throw new UsernameNotFoundException("empty username");
        }

        BackOfficeUser user = backOfficeUserRepository.findByAccountName(username);
        if (user == null) {
            throw new UsernameNotFoundException(username);
        } else {
            return new SecurityUserDetails(user);
        }
    }
}
