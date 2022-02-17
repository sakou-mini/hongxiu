package com.donglaistd.jinli.config.security.handler;

import com.donglaistd.jinli.http.entity.HttpURLConnection;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

import static com.donglaistd.jinli.constant.GameConstant.ACCOUNT_DISABLED_ERROR;
import static com.donglaistd.jinli.constant.GameConstant.LOGIN_FAILED;

@Component
public class AuthFailedHandler implements AuthenticationFailureHandler {
    private static final Logger logger = Logger.getLogger(AuthFailedHandler.class.getName());
    @Value("${security.login.username.key:username}")
    private String usernameKey;

    @Override
    public void onAuthenticationFailure(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
        int code = 0;
        if (e instanceof DisabledException) {
            code = ACCOUNT_DISABLED_ERROR;
        }else {
            code = LOGIN_FAILED;
        }
        httpServletResponse.setContentType("application/json;charset=utf-8");
        httpServletResponse.getWriter().write(new ObjectMapper().writeValueAsString(new HttpURLConnection<>(code,"登陆失败")));
    }
}
