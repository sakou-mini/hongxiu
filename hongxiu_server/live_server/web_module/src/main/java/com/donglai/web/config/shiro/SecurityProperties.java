package com.donglai.web.config.shiro;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:security-config.properties")
@Data
public class SecurityProperties {
    @Value("${security.ignore.resource}")
    private String[] securityIgnoreResource;

    @Value("${security.ignore.api}")
    private String[] securityIgnoreApi;

    @Value("${security.login.url}")
    private String loginApi;

    @Value("${security.logout.url}")
    private String logoutApi;

    @Value("${security.unAuth.url}")
    private String unAuthApi;

    @Value("${security.unauthorized.url}")
    private String unauthorizedApi;
}
