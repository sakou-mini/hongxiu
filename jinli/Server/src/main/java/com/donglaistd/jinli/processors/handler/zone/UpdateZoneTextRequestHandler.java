package com.donglaistd.jinli.processors.handler.zone;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.config.ZoneConfigProperties;
import com.donglaistd.jinli.database.dao.ZoneDaoService;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.zone.Zone;
import com.donglaistd.jinli.processors.handler.MessageHandler;
import com.donglaistd.jinli.util.DataManager;
import com.donglaistd.jinli.util.VerifyUtil;
import com.donglaistd.jinli.util.WordFilterUtil;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.donglaistd.jinli.Constant.ResultCode.CONTENT_WORDS_ILLEGAL;
import static com.donglaistd.jinli.util.MessageUtil.buildReply;

@Component
public class UpdateZoneTextRequestHandler extends MessageHandler {
    @Autowired
    ZoneDaoService zoneDaoService;
    @Autowired
    DataManager dataManager;
    @Autowired
    VerifyUtil verifyUtil;
    @Autowired
    ZoneConfigProperties zoneConfigProperties;
    @Autowired
    WordFilterUtil wordFilterUtil;

    @Override
    public Jinli.JinliMessageReply doHandle(ChannelHandlerContext ctx, Jinli.JinliMessageRequest messageRequest, User user) {
        Jinli.UpdateZoneTextRequest request = messageRequest.getUpdateZoneTextRequest();
        Jinli.UpdateZoneTextReply.Builder reply = Jinli.UpdateZoneTextReply.newBuilder();
        if(!verifyUtil.verifyIsLiveUser(dataManager.findLiveUser(user.getLiveUserId())))
            return buildReply(reply, Constant.ResultCode.NOT_LIVE_USER);

        String text = request.getText();
        if(Strings.isBlank(text) || text.length() > zoneConfigProperties.getSignatureMaxSize())
            return buildReply(reply, Constant.ResultCode.CONTENT_IS_EMPTY_OR_TOO_LONG);

        if(wordFilterUtil.containSensitiveWord(text)) return buildReply(reply, CONTENT_WORDS_ILLEGAL);

        Zone zone = zoneDaoService.findOrCreateZoneByUserId(user.getId());
        zone.setSignatureText(text);
        zoneDaoService.save(zone);
        return buildReply(reply.setText(text), Constant.ResultCode.SUCCESS);
    }

}
