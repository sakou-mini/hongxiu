package com.donglaistd.jinli.config;

import com.donglaistd.jinli.config.security.MyFilterSecurityInterceptor;
import com.donglaistd.jinli.config.security.handler.AuthFailedHandler;
import com.donglaistd.jinli.config.security.handler.SuccessHandler;
import com.donglaistd.jinli.database.entity.backoffice.BackOfficeRole;
import com.donglaistd.jinli.service.BackOfficeUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;

@Configuration
@EnableWebSecurity
public class BackOfficeSecurityConfig extends WebSecurityConfigurerAdapter {
    @Value("${security.ignore.resource}")
    private String[] securityIgnoreResource;
    @Value("${security.ignore.api}")
    private String[] securityIgnoreApi;
    @Value("${security.login.url}")
    private String loginApi;

    @Autowired
    BackOfficeUserDetailsService detailsService;
    @Autowired
    SuccessHandler successHandler;
    @Autowired
    AuthFailedHandler authFailedHandler;
    @Autowired
    private MyFilterSecurityInterceptor myFilterSecurityInterceptor;
    @Override
    public void configure(AuthenticationManagerBuilder builder) throws Exception {
        builder.userDetailsService(detailsService);
    }

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        http.csrf().disable().authorizeRequests()
                .antMatchers(HttpMethod.GET, securityIgnoreResource).permitAll()
                .antMatchers(securityIgnoreApi).permitAll()
                .antMatchers(loginApi).permitAll()
                //.mvcMatchers("/backOffice/backOfficeUser/admin/**").hasRole(BackOfficeRole.ADMIN.toString())
                .mvcMatchers("/api/v1/**").hasRole(BackOfficeRole.PLATFORM.toString())
                .mvcMatchers("/api/v2/**").hasRole(BackOfficeRole.PLATFORM_Q.toString())
                .anyRequest().authenticated()
                .and().formLogin().loginPage("/login")
                .successHandler(successHandler)
                .failureHandler(authFailedHandler)
                .and().logout()
                .logoutUrl("/logout").logoutSuccessUrl("/login").permitAll()
                .deleteCookies("JSESSIONID")
                .and().exceptionHandling().accessDeniedPage("/403");
        http.addFilterBefore(myFilterSecurityInterceptor, FilterSecurityInterceptor.class);
    }
}
