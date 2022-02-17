package com.donglaistd.jinli.processors.handler;

import com.donglaistd.jinli.BaseTest;
import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.dao.LiveUserDaoService;
import com.donglaistd.jinli.database.dao.UserDaoService;
import com.donglaistd.jinli.database.entity.LiveUser;
import com.donglaistd.jinli.util.DataManager;
import com.google.protobuf.ByteString;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.Attribute;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static com.donglaistd.jinli.processors.handler.MessageHandler.ROOM_KEY;
import static com.donglaistd.jinli.processors.handler.MessageHandler.USER_KEY;
import static java.awt.image.BufferedImage.TYPE_INT_RGB;

public class BecomeLiveUserRequestHandlerTest extends BaseTest {

    @Autowired
    BecomeLiveUserRequestHandler becomeLiveRequestHandler;

    @Autowired
    UserDaoService userDaoService;
    @Autowired
    LiveUserDaoService liveUserDaoService;

    @Test
    public void TestSuccessfulBecomeLiveUser() throws IOException {
        var requestWrapper = Jinli.JinliMessageRequest.newBuilder();
        var request = Jinli.BecomeLiveUserRequest.newBuilder();
        String email = "100000";
        request.setRealName("realName").setGender(Constant.GenderType.MALE)
                .setCountry("country").setAddress("address")
                .setPhoneNumber("phoneNumber").setEmail(email).setContactWay("email")
                .setBirthDay(System.currentTimeMillis() + "").setBankCard("12321").setBankName("chinaBank");
        var image = new BufferedImage(400, 400, TYPE_INT_RGB);
        var byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "png", byteArrayOutputStream);
        var out = ByteString.copyFrom(byteArrayOutputStream.toByteArray());
        //request.addImages(out);
        requestWrapper.setBecomeLiveUserRequest(request);
        user.setLiveUserId(null);
        liveUser.setUserId(null);
        dataManager.saveUser(user);
        dataManager.saveLiveUser(liveUser);
        becomeLiveRequestHandler.handle(context, requestWrapper.build());
        var user = userDaoService.findById(context.channel().attr(USER_KEY).get());
        Assert.assertNull(user.getLiveUserId());
        LiveUser liveUser = liveUserDaoService.findByUserId(user.getId());
        Assert.assertEquals(email, liveUser.getEmail());
        var room = DataManager.findOnlineRoom(liveUser.getRoomId());
        Assert.assertNull(room);
    }

//    @Test
    public void TestSuccessfulBecomeLiveUserConcurrently() throws IOException {
        ChannelHandlerContext cleanContext;
        cleanContext = Mockito.mock(ChannelHandlerContext.class);
        var cleanChannel = Mockito.mock(Channel.class);
        var attributeUser = Mockito.mock(Attribute.class);
        var attributeRoom = Mockito.mock(Attribute.class);
        Mockito.when(cleanContext.channel()).thenReturn(cleanChannel);
        Mockito.when(cleanChannel.attr(USER_KEY)).thenReturn(attributeUser);
        Mockito.when(cleanChannel.attr(ROOM_KEY)).thenReturn(attributeRoom);
        var cleanUser = userBuilder.createNoSavedUser("aaa", "bbb", "", "mock_token",0,"1234567890",true);
        Mockito.when(attributeUser.get()).thenReturn(cleanUser.getId());
        Mockito.when(cleanContext.channel()).thenReturn(cleanChannel);

        var requestWrapper = Jinli.JinliMessageRequest.newBuilder();
        var request = Jinli.BecomeLiveUserRequest.newBuilder();
        String email = "100000";
        request.setRealName("realName").setGender(Constant.GenderType.MALE)
                .setCountry("country").setAddress("address")
                .setPhoneNumber("phoneNumber").setEmail(email).setContactWay("email")
                .setBirthDay(System.currentTimeMillis() + "");
        var image = new BufferedImage(400, 400, TYPE_INT_RGB);
        var byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "png", byteArrayOutputStream);
        var out = ByteString.copyFrom(byteArrayOutputStream.toByteArray());
        //request.addImages(out);
        requestWrapper.setBecomeLiveUserRequest(request);
        cleanUser.setLiveUserId(null);
        cleanUser.increaseModifyNameCount();
        dataManager.saveUser(cleanUser);
        becomeLiveRequestHandler.handle(cleanContext, requestWrapper.build());
        cleanUser = userDaoService.findById(cleanContext.channel().attr(USER_KEY).get());
        Assert.assertNotNull(cleanUser.getLiveUserId());
        LiveUser onlineLiveUser = dataManager.findLiveUser(cleanUser.getLiveUserId());
        var room = DataManager.roomMap.get(onlineLiveUser.getRoomId());
        Assert.assertNotNull(room);
        Assert.assertEquals(email, onlineLiveUser.getEmail());
    }
}
