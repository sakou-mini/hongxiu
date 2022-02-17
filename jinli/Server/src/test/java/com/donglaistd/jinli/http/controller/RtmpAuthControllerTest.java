package com.donglaistd.jinli.http.controller;

import com.donglaistd.jinli.BaseTest;
import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.dao.RoomDaoService;
import com.donglaistd.jinli.database.dao.UserDaoService;
import com.donglaistd.jinli.database.entity.LiveUser;
import com.donglaistd.jinli.processors.handler.BecomeLiveUserRequestHandler;
import com.donglaistd.jinli.processors.handler.LoginRequestHandler;
import com.donglaistd.jinli.processors.handler.StartLiveRequestHandler;
import com.google.protobuf.ByteString;
import io.netty.channel.ChannelHandlerContext;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.InvocationTargetException;

import static com.donglaistd.jinli.processors.handler.MessageHandler.USER_KEY;
import static java.awt.image.BufferedImage.TYPE_INT_RGB;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RtmpAuthControllerTest extends BaseTest {

    @Autowired
    RtmpAuthController controller;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    UserDaoService userDaoService;
    @Autowired
    RoomDaoService roomDaoService;
    @Autowired
    LoginRequestHandler loginRequestHandler;
    @Autowired
    BecomeLiveUserRequestHandler becomeLiveRequestHandler;
    @Autowired
    StartLiveRequestHandler startLiveRequestHandler;
    private MockMvc mockMvc;

    @Before
    @Override
    public void setUp() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        super.setUp();
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    public void RtmpAutoFailTest() throws Exception {
        String url = "/rtmp/auth?userId=1&code=2";
        RequestBuilder request = MockMvcRequestBuilders.post(url);
        mockMvc.perform(request).andExpect(MockMvcResultMatchers.status().is5xxServerError()).andExpect(MockMvcResultMatchers.content().string("error"));
    }

    @Test
    public void RtmpAutoSuccessTest() throws Exception {
        String accountName = user.getAccountName();
        String password = "password";
        user.setToken(passwordEncoder.encode(password));
        dataManager.saveUser(user);
        roomDaoService.save(room);

        var requestWrapper = Jinli.JinliMessageRequest.newBuilder();
        var request = Jinli.LoginRequest.newBuilder();
        request.setAccount(accountName);
        request.setPassword(password);
        requestWrapper.setLoginRequest(request);
        Jinli.JinliMessageReply reply = loginRequestHandler.handle(context, requestWrapper.build());
        Assert.assertEquals(Constant.ResultCode.SUCCESS, reply.getResultCode());

        var loginReply = reply.getLoginReply();
        var userInfo = loginReply.getUserInfo();

        toLiveUser(context);
        startLive(context);

        String userId = userInfo.getUserId();
        var newUser = userDaoService.findById(context.channel().attr(USER_KEY).get());
        String rtmpCode = dataManager.findLiveUser(newUser.getLiveUserId()).getRtmpCode();
        String rtmpName = dataManager.findLiveUser(newUser.getLiveUserId()).getLiveUrl();
        String url = "/rtmp/auth?userId=" + userId + "&code=" + rtmpCode + "&name=" + rtmpName;
        RequestBuilder rtmpAuth = MockMvcRequestBuilders.post(url);

        mockMvc.perform(rtmpAuth).andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().string("success"));
    }

    private void startLive(ChannelHandlerContext context) {
        var requestWrapper = Jinli.JinliMessageRequest.newBuilder();
        var request = Jinli.StartLiveRequest.newBuilder();
        request.setPattern(Constant.Pattern.LIVE_VIDEO);
        requestWrapper.setStartLiveRequest(request);
        Jinli.JinliMessageReply reply = startLiveRequestHandler.handle(context, requestWrapper.build());
        var liveReply = reply.getStartLiveReply();
        String rtmpCode = liveReply.getRtmpCode();
        var user = userDaoService.findById(context.channel().attr(USER_KEY).get());
       // dataManager.findLiveUser(user.getLiveUserId()).setRtmpCode(rtmpCode);
    }

    private void toLiveUser(ChannelHandlerContext context) throws Exception {
        var requestWrapper = Jinli.JinliMessageRequest.newBuilder();
        var request = Jinli.BecomeLiveUserRequest.newBuilder();
        String description = "10000";
        request.setRealName("realName").setGender(Constant.GenderType.MALE)
                .setCountry("country").setAddress("address")
                .setPhoneNumber("phoneNumber").setEmail("12345@@@.com").setContactWay("email")
                .setBirthDay(System.currentTimeMillis()+"");
        var image = new BufferedImage(400, 400, TYPE_INT_RGB);
        var byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "png", byteArrayOutputStream);
        var out = ByteString.copyFrom(byteArrayOutputStream.toByteArray());
        //request.addImages(out);
        requestWrapper.setBecomeLiveUserRequest(request);
        becomeLiveRequestHandler.handle(context, requestWrapper.build());

        var user = userDaoService.findById(context.channel().attr(USER_KEY).get());
        LiveUser onlineLiveUser = dataManager.findLiveUser(user.getLiveUserId());
        Assert.assertNotNull(onlineLiveUser);
        Assert.assertNotNull(onlineLiveUser.getRoomId());
    }

    @Test
    public void RtmpDoneFailTest() throws Exception {
        String url = "/rtmp/done?userId=1&code=2";
        RequestBuilder request = MockMvcRequestBuilders.post(url);

        mockMvc.perform(request).andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().string("error"));
    }

    @Test
    public void RtmpDoneSuccessTest() throws Exception {
        var user = userDaoService.findById(context.channel().attr(USER_KEY).get());
        String accountName = user.getAccountName();
        String password = "password";
        user.setAccountName(accountName);
        user.setDisplayName(accountName);
        user.setToken(passwordEncoder.encode(password));
        userDaoService.save(user);
        roomDaoService.save(room);
        dataManager.saveUser(user);

        var requestWrapper = Jinli.JinliMessageRequest.newBuilder();
        var request = Jinli.LoginRequest.newBuilder();
        request.setAccount(accountName);
        request.setPassword(password);
        requestWrapper.setLoginRequest(request);
        Jinli.JinliMessageReply reply = loginRequestHandler.handle(context, requestWrapper.build());
        Assert.assertEquals(Constant.ResultCode.SUCCESS, reply.getResultCode());

        var loginReply = reply.getLoginReply();
        var userInfo = loginReply.getUserInfo();

        toLiveUser(context);
        startLive(context);

        String userId = userInfo.getUserId();
        var newUser = userDaoService.findById(context.channel().attr(USER_KEY).get());
        String rtmpCode = dataManager.findLiveUser(newUser.getLiveUserId()).getRtmpCode();
        String rtmpName = dataManager.findLiveUser(newUser.getLiveUserId()).getLiveUrl();
        String url = "/rtmp/auth?userId=" + userId + "&code=" + rtmpCode + "&name=" + rtmpName;
        RequestBuilder rtmpAuth = MockMvcRequestBuilders.post(url);

        mockMvc.perform(rtmpAuth).andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().string("success"));

        url = "/rtmp/done?userId=" + userId + "&code=" + rtmpCode + "&name=" + dataManager.findLiveUser(newUser.getLiveUserId()).getLiveUrl();
        RequestBuilder rtmpdone = MockMvcRequestBuilders.post(url);

        mockMvc.perform(rtmpdone).andExpect(MockMvcResultMatchers.status().isOk()).andExpect(MockMvcResultMatchers.content().string("success"));
    }
}
