package com.donglaistd.jinli.config.security.handler;

import com.donglaistd.jinli.constant.GameConstant;
import com.donglaistd.jinli.database.dao.BackOfficeUserDaoService;
import com.donglaistd.jinli.database.entity.backoffice.BackOfficeUser;
import com.donglaistd.jinli.database.entity.system.Menu;
import com.donglaistd.jinli.http.entity.HttpURLConnection;
import com.donglaistd.jinli.service.MenuProcess;
import com.donglaistd.jinli.service.SecurityUserDetails;
import com.donglaistd.jinli.util.VerifyUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class SuccessHandler implements AuthenticationSuccessHandler {
    @Autowired
    VerifyUtil verifyUtil;
    @Autowired
    MenuProcess menuProcess;
    @Autowired
    BackOfficeUserDaoService backOfficeUserDaoService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
        httpServletResponse.setStatus(200);
        SecurityUserDetails details = (SecurityUserDetails) authentication.getPrincipal();
        if(verifyUtil.isPlatformAccount(details.getUsername())){
            httpServletResponse.setContentType("application/json;charset=utf-8");
            httpServletResponse.getWriter().write(new ObjectMapper().writeValueAsString("success"));
        } else {
            /*httpServletResponse.getWriter().write(new ObjectMapper().writeValueAsString(details));
            httpServletResponse.setContentType("text/html;charset=UTF-8");
            httpServletResponse.sendRedirect("/backOffice/index");*/
            httpServletResponse.setContentType("application/json;charset=utf-8");
            BackOfficeUser backOfficeUser = backOfficeUserDaoService.findByAccountName(details.getUsername());
            HttpURLConnection<Menu> result = new HttpURLConnection<>(GameConstant.SUCCESS, "success");
            result.data = menuProcess.getMenuByBackOfficeUser(backOfficeUser);
            httpServletResponse.getWriter().write(new ObjectMapper().writeValueAsString(result));
        }
    }
}
