package com.donglaistd.jinli.processors.handler;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.util.DataManager;
import com.google.protobuf.ByteString;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

@Component
public abstract class MessageHandler {

    private static final Logger logger = Logger.getLogger(MessageHandler.class.getName());

    public static final AttributeKey<String> USER_KEY = AttributeKey.newInstance("user_key");
    public static final AttributeKey<String> ROOM_KEY = AttributeKey.newInstance("room_key");
    public static final AttributeKey<String> DOMAIN_KEY = AttributeKey.newInstance("domain_key");
    public static final AttributeKey<String> IP_KEY = AttributeKey.newInstance("ip_key");

    protected Constant.ResultCode resultCode = Constant.ResultCode.SUCCESS;

    @Autowired
    protected DataManager dataManager;

    public Jinli.JinliMessageReply handle(ChannelHandlerContext ctx, Jinli.JinliMessageRequest messageRequest) {
        return doHandle(ctx, messageRequest, dataManager.getUserFromChannel(ctx));
    }

    protected abstract Jinli.JinliMessageReply doHandle(ChannelHandlerContext ctx, Jinli.JinliMessageRequest messageRequest, User user);

    protected Constant.ResultCode saveImage(ByteString bytes, String savePath, int width, int height, String timeString, User user) {
        BufferedImage image = null;
        Constant.ResultCode code = Constant.ResultCode.UNKNOWN;
        try {
            image = ImageIO.read(new BufferedInputStream(new ByteArrayInputStream(bytes.toByteArray())));
        } catch (IOException e) {
            logger.warning("io error while reading image data");
            code = Constant.ResultCode.DATA_FORMAT_MALFORMED;
        }

        if (image != null) {
            if ((width > 0 && height > 0) && (image.getWidth() != width || image.getHeight() != height)) {
                code = Constant.ResultCode.DATA_FORMAT_MALFORMED;
            } else {
                var file = new File(savePath + "/" + user.getId() + "/" + timeString + ".png");
                if (file.mkdirs()) {
                    try {
                        ImageIO.write(image, "png", file);
                        code = Constant.ResultCode.SUCCESS;
                    } catch (IOException e) {
                        logger.warning("io error while saving image data");
                        code = Constant.ResultCode.UNKNOWN;
                    }
                } else {
                    logger.warning("creating image file error");
                    code = Constant.ResultCode.UNKNOWN;
                }
            }
        }
        return code;
    }
}