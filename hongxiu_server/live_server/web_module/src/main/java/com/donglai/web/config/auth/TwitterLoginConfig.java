package com.donglai.web.config.auth;

import lombok.Getter;
import lombok.Setter;
import me.zhyd.oauth.config.AuthConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author Moon
 * @date 2021-10-26 10:43
 */
@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "twitter")
public class TwitterLoginConfig extends AuthConfig {

    @Value("client_id")
    private String clientId;

    @Value("client_secret")
    private String clientSecret;

    @Value("redirect_uri")
    private String redirectUri;

}
