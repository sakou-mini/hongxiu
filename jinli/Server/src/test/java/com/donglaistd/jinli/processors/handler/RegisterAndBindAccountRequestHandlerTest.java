package com.donglaistd.jinli.processors.handler;

import com.donglaistd.jinli.BaseTest;
import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.dao.UserDaoService;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.Attribute;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static com.donglaistd.jinli.processors.handler.MessageHandler.ROOM_KEY;
import static com.donglaistd.jinli.processors.handler.MessageHandler.USER_KEY;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RegisterAndBindAccountRequestHandlerTest extends BaseTest {

    @Autowired
    RegisterRequestHandler registerRequestHandler;

    @Autowired
    ChangePasswordRequestHandler changePasswordRequestHandler;

    @Autowired
    UserDaoService userDaoService;

    @Test
    public void TestSuccessfulRegister() {
        var cleanContext = Mockito.mock(ChannelHandlerContext.class);
        var cleanChannel = Mockito.mock(Channel.class);
        Mockito.when(cleanContext.channel()).thenReturn(cleanChannel);
        Mockito.when(cleanChannel.attr(USER_KEY)).thenReturn(null);
        Mockito.when(cleanChannel.attr(ROOM_KEY)).thenReturn(null);
        Mockito.when(cleanContext.channel()).thenReturn(cleanChannel);

        var request = Jinli.JinliMessageRequest.newBuilder();
        var registerRequest = Jinli.RegisterRequest.newBuilder();
        String accountName = "registerAccount";
        var user = userDaoService.findByAccountName(accountName);
        Assert.assertNull(user);
        registerRequest.setAccountName(accountName);
        String displayName = "nickname";
        registerRequest.setDisplayName(displayName);
        request.setRegisterRequest(registerRequest);
        var result = registerRequestHandler.handle(cleanContext, request.build());
        user = userDaoService.findByAccountName(accountName);
        Assert.assertEquals(accountName, user.getAccountName());
        Assert.assertEquals(displayName, user.getDisplayName());
        Assert.assertEquals(Constant.ResultCode.SUCCESS, result.getResultCode());
    }

    @Test
    public void TestRegisterFailedWithLongName() {
        var request = Jinli.JinliMessageRequest.newBuilder();
//        var bindAccountRequest = Jinli.BindAccountRequest.newBuilder();
//        bindAccountRequest.setRequestAccountName("tooLongAccount").setRequestPassword("newPassword");
//        request.setBindAccountRequest(bindAccountRequest);
//        var result = bindAccountRequestHandler.handle(context, request.build());
//        Assert.assertEquals(Constant.ResultCode.MISSING_OR_ILLEGAL_PARAMETERS, result.getResultCode());
//
//        bindAccountRequest = Jinli.BindAccountRequest.newBuilder();
//        bindAccountRequest.setRequestAccountName("newAcc").setRequestPassword("newPassword");
//        request.setBindAccountRequest(bindAccountRequest);
//        result = bindAccountRequestHandler.handle(context, request.build());
//        Assert.assertEquals(Constant.ResultCode.NOT_TOURIST_BIND_ACCOUNT, result.getResultCode());
    }

    @Test
    public void TestSuccessfulRegisterWithEmptyNameAsTourist() {
//        var request = Jinli.JinliMessageRequest.newBuilder();
//        var registerRequest = Jinli.RegisterRequest.newBuilder();
//        String accountName = "registerAccount";
//        var user = userDaoService.findByAccountName(accountName);
//        Assert.assertNull(user);
//        registerRequest.setAccountName(accountName);
//        request.setRegisterRequest(registerRequest);
//        registerRequestHandler.handle(context, request.build());
//        user = userDaoService.findByAccountName(accountName);
//        Assert.assertEquals(accountName, user.getAccountName());
//        Assert.assertTrue(user.getDisplayName().startsWith("游客_"));
//        var bindAccountRequest = Jinli.BindAccountRequest.newBuilder();
//        bindAccountRequest.setRequestAccountName("newAcc").setRequestPassword("newPassword");
//        request.setBindAccountRequest(bindAccountRequest);
//        var result = changePasswordRequestHandler.doHandle(context, request.build(), user);
//        Assert.assertEquals(Constant.ResultCode.SUCCESS, result.getResultCode());
//        Assert.assertFalse(user.isTourist());
    }

    @Test
    public void TestSuccessfulRegisterConcurrency() throws InterruptedException {
        var cleanContext = Mockito.mock(ChannelHandlerContext.class);
        var cleanChannel = Mockito.mock(Channel.class);
        Mockito.when(cleanContext.channel()).thenReturn(cleanChannel);
        Mockito.when(cleanChannel.attr(USER_KEY)).thenReturn(null);
        Mockito.when(cleanChannel.attr(ROOM_KEY)).thenReturn(null);
        Mockito.when(cleanContext.channel()).thenReturn(cleanChannel);

        var request = Jinli.JinliMessageRequest.newBuilder();
        var registerRequest = Jinli.RegisterRequest.newBuilder();
        String accountName = "registerAccount";
        registerRequest.setAccountName(accountName);
        String displayName = "nickname";
        registerRequest.setDisplayName(displayName);
        request.setRegisterRequest(registerRequest);

        var thread = new Thread(() -> registerRequestHandler.handle(cleanContext, request.build()));
        var thread2 = new Thread(() -> registerRequestHandler.handle(cleanContext, request.build()));
        thread.start();
        thread2.start();

        Thread.sleep(1000);
        var user = userDaoService.findByAccountName(accountName);
        Assert.assertEquals(accountName, user.getAccountName());
        Assert.assertEquals(displayName, user.getDisplayName());
    }


    @Test
    public void TestRegisterFailedWithDifferentCaseAccountName() {
        var request = Jinli.JinliMessageRequest.newBuilder();
        var registerRequest = Jinli.RegisterRequest.newBuilder();
        String accountName = "registerAccount";
        registerRequest.setAccountName(accountName);
        String displayName = "nickname";
        registerRequest.setDisplayName(displayName);
        request.setRegisterRequest(registerRequest);
        var result = registerRequestHandler.handle(context, request.build());
        user = userDaoService.findByAccountName(accountName);
        Assert.assertEquals(accountName, user.getAccountName());
        Assert.assertEquals(displayName, user.getDisplayName());
        Assert.assertEquals(Constant.ResultCode.SUCCESS, result.getResultCode());

        registerRequest = Jinli.RegisterRequest.newBuilder();
        registerRequest.setAccountName("registeraccount").setDisplayName("nickname2");
        request.setRegisterRequest(registerRequest);
        result = registerRequestHandler.handle(context, request.build());
        Assert.assertEquals(Constant.ResultCode.ACCOUNT_NAME_ALREADY_EXIST, result.getResultCode());
    }
}
