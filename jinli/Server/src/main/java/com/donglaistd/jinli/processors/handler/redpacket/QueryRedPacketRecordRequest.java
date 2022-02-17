package com.donglaistd.jinli.processors.handler.redpacket;

import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.dao.LiveUserDaoService;
import com.donglaistd.jinli.database.dao.RedPacketRecordDaoService;
import com.donglaistd.jinli.database.dao.UserDaoService;
import com.donglaistd.jinli.database.entity.RedPacketRecord;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.processors.handler.MessageHandler;
import com.donglaistd.jinli.util.DataManager;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.donglaistd.jinli.util.MessageUtil.buildReply;
/**
 * @deprecated
 */
@Component
public class QueryRedPacketRecordRequest extends MessageHandler {
    @Autowired
    RedPacketRecordDaoService redPacketRecordDaoService;
    @Autowired
    UserDaoService userDaoService;

    @Override
    public Jinli.JinliMessageReply doHandle(ChannelHandlerContext ctx, Jinli.JinliMessageRequest messageRequest, User user) {
        var request = messageRequest.getQueryRedPacketRecordRequest();
        var reply = Jinli.QueryRedPacketRecordReply.newBuilder();
        var redPacketId = request.getRedPacketId();
        var records = redPacketRecordDaoService.findByRedPacketId(redPacketId);
        User currentUser;
        for (RedPacketRecord record : records) {
            currentUser = userDaoService.findById(record.getUserId());
            reply.addRedPacketRecords(Jinli.RedPacketRecord.newBuilder().setCoin(record.getAmount()).setUserInfo(currentUser.toSummaryProto()).setOpenTime(record.getGrabTime()));
        }
        return buildReply(reply);
    }
}
