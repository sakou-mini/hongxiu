package com.donglai.web.config.request;

import me.zhyd.oauth.cache.AuthStateCache;
import me.zhyd.oauth.config.AuthConfig;
import me.zhyd.oauth.model.AuthCallback;
import me.zhyd.oauth.model.AuthToken;
import me.zhyd.oauth.model.AuthUser;
import me.zhyd.oauth.request.AuthFacebookRequest;
import me.zhyd.oauth.utils.UrlBuilder;

/**
 * @author Moon
 * @date 2021-12-07 17:06
 */
public class AuthFacebookRequest2 extends AuthFacebookRequest {
    public AuthFacebookRequest2(AuthConfig config) {
        super(config);
    }

    public AuthFacebookRequest2(AuthConfig config, AuthStateCache authStateCache) {
        super(config, authStateCache);
    }

    @Override
    protected AuthToken getAccessToken(AuthCallback authCallback) {
        return super.getAccessToken(authCallback);
    }

    @Override
    protected AuthUser getUserInfo(AuthToken authToken) {
        return super.getUserInfo(authToken);
    }

    @Override
    protected String userInfoUrl(AuthToken authToken) {
        return super.userInfoUrl(authToken);
    }

    @Override
    public String authorize(String state) {
        return UrlBuilder.fromBaseUrl(source.authorize())
                .queryParam("response_type", "code")
                .queryParam("client_id", config.getClientId())
                .queryParam("redirect_uri", config.getRedirectUri())
                .queryParam("state", getRealState(state))
                .queryParam("scope", "groups_access_member_info"
                        + ",publish_to_groups"
                        + ",email"
                ).build();
    }
}
