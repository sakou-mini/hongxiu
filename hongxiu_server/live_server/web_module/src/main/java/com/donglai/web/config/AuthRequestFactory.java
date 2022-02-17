package com.donglai.web.config;

import com.donglai.web.config.auth.FacebookLoginConfig;
import com.donglai.web.config.auth.GoogleLoginConfig;
import com.donglai.web.config.auth.TwitterLoginConfig;
import com.donglai.web.config.request.AuthFacebookRequest2;
import com.donglai.web.config.request.AuthTwitterRequest2;
import com.xkcoding.http.config.HttpConfig;
import me.zhyd.oauth.enums.scope.AuthFacebookScope;
import me.zhyd.oauth.request.AuthGoogleRequest;
import me.zhyd.oauth.request.AuthRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Arrays;

/**
 * @author Moon
 * @date 2021-10-26 11:29
 */
@Component
public class AuthRequestFactory {
    @Autowired
    private GoogleLoginConfig googleLoginConfig;

    @Autowired
    private TwitterLoginConfig twitterLoginConfig;

    @Autowired
    private FacebookLoginConfig facebookLoginConfig;

    public AuthRequest getAuthRequest(String source) {
        //HttpConfig config = new HttpConfig();
        //config.setProxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 10809)));
        //config.setTimeout(15000);
        if ("GOOGLE".equals(source.toUpperCase())) {
            //googleLoginConfig.setHttpConfig(config);
            return new AuthGoogleRequest(googleLoginConfig);
        }
        if ("TWITTER".equals(source.toUpperCase())) {
            //twitterLoginConfig.setHttpConfig(config);
            return new AuthTwitterRequest2(twitterLoginConfig);
        }
        if ("FACEBOOK".equals(source.toUpperCase())) {
            //facebookLoginConfig.setHttpConfig(config);
            facebookLoginConfig.setScopes(Arrays.asList(
                    AuthFacebookScope.GROUPS_ACCESS_MEMBER_INFO.getScope(),
                    AuthFacebookScope.PUBLISH_TO_GROUPS.getScope(),
                    AuthFacebookScope.EMAIL.getScope()
            ));
            return new AuthFacebookRequest2(facebookLoginConfig);
        }
        return null;
    }
}
