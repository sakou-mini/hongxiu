package com.donglaistd.jinli.processors.handler;

import com.donglaistd.jinli.BaseTest;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.dao.UserDaoService;
import com.google.protobuf.ByteString;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static com.donglaistd.jinli.processors.handler.MessageHandler.USER_KEY;
import static java.awt.image.BufferedImage.TYPE_INT_RGB;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UpdateAvatarRequestHandlerTest extends BaseTest {

    @Autowired
    UpdateUserInfoRequestHandler updateUserInfoRequestHandler;
    @Autowired
    UserDaoService userDaoService;

    @Test

    public void UpdateAvatarTest() throws IOException {
        userDaoService.save(user);
        var request = Jinli.JinliMessageRequest.newBuilder();
        var updateAvatarRequest = Jinli.UpdateUserInfoRequest.newBuilder();
        var image = new BufferedImage(200, 200, TYPE_INT_RGB);
        var byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "png", byteArrayOutputStream);
        var out = ByteString.copyFrom(byteArrayOutputStream.toByteArray());
        updateAvatarRequest.setAvatarData(out);
        request.setUpdateUserInfoRequest(updateAvatarRequest);

        updateUserInfoRequestHandler.handle(context, request.build());
        var user = userDaoService.findById(context.channel().attr(USER_KEY).get());
        Assert.assertTrue(user.getAvatarUrl().length() > 14);
    }
}
