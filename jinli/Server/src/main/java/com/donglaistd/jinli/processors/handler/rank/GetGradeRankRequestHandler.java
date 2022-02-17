package com.donglaistd.jinli.processors.handler.rank;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.processors.handler.MessageHandler;
import com.donglaistd.jinli.util.DataManager;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.stereotype.Component;

import static com.donglaistd.jinli.util.MessageUtil.buildReply;
/**
 * @deprecated
 */
@Component
public class GetGradeRankRequestHandler extends MessageHandler {
    @Override
    public Jinli.JinliMessageReply doHandle(ChannelHandlerContext ctx, Jinli.JinliMessageRequest messageRequest, User user) {
        Jinli.GetGradeRankReply.Builder builder = Jinli.GetGradeRankReply.newBuilder();
        resultCode = Constant.ResultCode.SUCCESS;
        return buildReply(builder.addAllGrade(DataManager.gradeRank).build(), resultCode);
    }
}
