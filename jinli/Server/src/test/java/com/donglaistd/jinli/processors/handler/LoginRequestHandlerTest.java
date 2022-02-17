package com.donglaistd.jinli.processors.handler;

import com.donglaistd.jinli.BaseTest;
import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.dao.UserDaoService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class LoginRequestHandlerTest extends BaseTest {

    @Autowired
    LoginRequestHandler loginRequestHandler;

    @Autowired
    RegisterRequestHandler registerRequestHandler;

    @Autowired
    UserDaoService userDaoService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Test

    @Rollback
    public void TestSuccessfulLogin() {
        var requestWrapper = Jinli.JinliMessageRequest.newBuilder();
        var request = Jinli.LoginRequest.newBuilder();
        String accountName = "tester";
        request.setAccount(accountName);
        String password = "password";
        request.setPassword(password);

        var user = userBuilder.createUser(accountName, accountName, passwordEncoder.encode(password));
        requestWrapper.setLoginRequest(request);

        loginRequestHandler.handle(context, requestWrapper.build());
        Assert.assertEquals(Constant.ResultCode.SUCCESS, loginRequestHandler.resultCode);
    }

    @Test

    @Rollback
    public void TestLoginFailed() {
        var requestWrapper = Jinli.JinliMessageRequest.newBuilder();
        var request = Jinli.LoginRequest.newBuilder();
        String accountName = "tester";
        request.setAccount(accountName);
        String password = "password";
        request.setPassword(password);

        var user = userBuilder.createUser(accountName, accountName ,"wrongPassword");
        requestWrapper.setLoginRequest(request);

        var result = loginRequestHandler.handle(context, requestWrapper.build());
        Assert.assertEquals(Constant.ResultCode.PASSWORD_ERROR, result.getResultCode());
    }

    @Test

    @Rollback
    public void TestUserLoginAfterRegisterWithChosenPassword() {
        var request = Jinli.JinliMessageRequest.newBuilder();
        var registerRequest = Jinli.RegisterRequest.newBuilder();
        String accountName = "abcd";
        var user = userDaoService.findByAccountName(accountName);
        Assert.assertNull(user);
        registerRequest.setAccountName(accountName);
        String displayName = "nickname";
        registerRequest.setDisplayName(displayName);
        String password = "a_password";
        registerRequest.setPassword(password);
        request.setRegisterRequest(registerRequest);
        registerRequestHandler.handle(context, request.build());

        var loginRequest = Jinli.LoginRequest.newBuilder();
        loginRequest.setAccount(accountName);
        loginRequest.setPassword(password);
        request.setLoginRequest(loginRequest);
        var result = loginRequestHandler.handle(context, request.build());
        Assert.assertEquals(Constant.ResultCode.SUCCESS, result.getResultCode());
    }
}
