package com.donglaistd.jinli.processors.handler;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.service.UploadServerService;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.donglaistd.jinli.constant.GameConstant.HTTP_URL;
import static com.donglaistd.jinli.util.MessageUtil.buildReply;

@Component
public class QueryResourceServerURLRequestHandler extends MessageHandler{
    @Autowired
    UploadServerService uploadServerService;

    @Override
    protected Jinli.JinliMessageReply doHandle(ChannelHandlerContext ctx, Jinli.JinliMessageRequest messageRequest, User user) {
        String resourceService = uploadServerService.getActiveUpload();
        resourceService = HTTP_URL + resourceService;
        Jinli.QueryResourceServerURLReply.Builder reply = Jinli.QueryResourceServerURLReply.newBuilder().setResourceServer(resourceService);
        return buildReply(reply.build(), Constant.ResultCode.SUCCESS);
    }
}
