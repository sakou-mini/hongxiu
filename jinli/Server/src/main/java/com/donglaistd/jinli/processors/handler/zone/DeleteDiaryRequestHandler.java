package com.donglaistd.jinli.processors.handler.zone;

import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.dao.DiaryReplyDaoService;
import com.donglaistd.jinli.database.dao.PersonDiaryDaoService;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.zone.PersonDiary;
import com.donglaistd.jinli.processors.handler.MessageHandler;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

import static com.donglaistd.jinli.Constant.ResultCode.*;
import static com.donglaistd.jinli.util.MessageUtil.buildReply;

@Component
public class DeleteDiaryRequestHandler extends MessageHandler {
    @Autowired
    PersonDiaryDaoService diaryDaoService;
    @Autowired
    DiaryReplyDaoService diaryReplyDaoService;

    @Override
    @Transactional
    public Jinli.JinliMessageReply doHandle(ChannelHandlerContext ctx, Jinli.JinliMessageRequest messageRequest, User user) {
        Jinli.DeleteDiaryRequest request = messageRequest.getDeleteDiaryRequest();
        Jinli.DeleteDiaryReply.Builder reply = Jinli.DeleteDiaryReply.newBuilder();
        PersonDiary diary = diaryDaoService.findById(request.getDiaryId());
        if(Objects.isNull(diary)) return buildReply(reply, DIARY_NOTFOUND);
        else if(!Objects.equals(diary.getUserId(),user.getId()))  return buildReply(reply, NOT_DIARY_OWNER);
        diaryDaoService.deleteAndCleanDiaryCache(diary);
        diaryReplyDaoService.deleteAllReplyByDiaryId(diary.getId());
        dataManager.removeDiaryFromDiaryIds(diary.getId());
        return buildReply(reply.setDiaryId(diary.getId()), SUCCESS);
    }
}
