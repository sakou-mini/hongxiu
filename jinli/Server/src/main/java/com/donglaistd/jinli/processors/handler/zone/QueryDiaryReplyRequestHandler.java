package com.donglaistd.jinli.processors.handler.zone;

import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.dao.DiaryReplyDaoService;
import com.donglaistd.jinli.database.dao.PersonDiaryDaoService;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.zone.DiaryReply;
import com.donglaistd.jinli.database.entity.zone.PersonDiary;
import com.donglaistd.jinli.processors.handler.MessageHandler;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

import static com.donglaistd.jinli.Constant.ResultCode.DIARY_NOTFOUND;
import static com.donglaistd.jinli.Constant.ResultCode.SUCCESS;
import static com.donglaistd.jinli.util.MessageUtil.buildReply;

@Component
public class QueryDiaryReplyRequestHandler extends MessageHandler {
    @Autowired
    DiaryReplyDaoService replyDaoService;
    @Autowired
    PersonDiaryDaoService diaryDaoService;

    @Override
    public Jinli.JinliMessageReply doHandle(ChannelHandlerContext ctx, Jinli.JinliMessageRequest messageRequest, User user) {

        Jinli.QueryDiaryReplyRequest request = messageRequest.getQueryDiaryReplyRequest();
        Jinli.QueryDiaryReplyReply.Builder reply = Jinli.QueryDiaryReplyReply.newBuilder();
        String diaryId = request.getDiaryId();
        String replyId = request.getReplyId();
        int page = request.getPage();
        int rows = request.getRows();
        List<DiaryReply> replyList;
        if (Strings.isBlank(replyId)) replyList = replyDaoService.findPageRootReply(diaryId,page,rows,request.getExcludeReplyIdsList());
        else replyList = replyDaoService.findAllSecondReply(replyId,request.getExcludeReplyIdsList());
        PersonDiary diary = diaryDaoService.findById(diaryId);
        if(Objects.isNull(diary)) return buildReply(reply, DIARY_NOTFOUND);
        for (DiaryReply diaryReply : replyList) {
            reply.addReply(diaryReply.toProto());
        }
        return buildReply(reply, SUCCESS);
    }
}
