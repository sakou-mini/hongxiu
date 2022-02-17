package com.donglaistd.jinli.service;

import com.donglaistd.jinli.database.entity.backoffice.BackOfficeRole;
import com.donglaistd.jinli.database.entity.backoffice.BackOfficeUser;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static com.donglaistd.jinli.constant.BackOfficeConstant.ROLE_PREFIX;

public class SecurityUserDetails implements UserDetails {
    private final BackOfficeUser user;

    private final Set<GrantedAuthority> authorities = new HashSet<>();

    public SecurityUserDetails(BackOfficeUser user) {
        this.user = user;
        for (BackOfficeRole role : user.getRoles()) {
            this.authorities.add(new SimpleGrantedAuthority(ROLE_PREFIX + role.name()));
        }

    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getToken();
    }

    @Override
    public String getUsername() {
        return user.getAccountName();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return user.getRoles().contains(BackOfficeRole.LOGIN) || user.getRoles().contains(BackOfficeRole.ADMIN);
    }
}
