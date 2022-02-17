package com.donglai.test.handle.message;

import com.donglai.protocol.Constant;
import com.donglai.protocol.HongXiu;
import com.donglai.test.entity.UserCache;
import com.donglai.test.handle.MessageHandler;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@Slf4j
public class LiveOfApplyLiveUserReplyHandler extends MessageHandler {

    @Override
    protected void doHandle(ChannelHandlerContext ctx, HongXiu.HongXiuMessageReply reply, UserCache userCache) {
        Constant.ResultCode resultCode = reply.getResultCode();
        if (Objects.equals(Constant.ResultCode.SUCCESS, resultCode)) {
            log.info("APPLY LIVE USER SUCCESS");
        }
    }


}
