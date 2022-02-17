package com.donglaistd.jinli.processors.handler.zone;

import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.config.ConditionType;
import com.donglaistd.jinli.database.dao.DiaryStarDaoService;
import com.donglaistd.jinli.database.dao.PersonDiaryDaoService;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.zone.DiaryStar;
import com.donglaistd.jinli.database.entity.zone.PersonDiary;
import com.donglaistd.jinli.event.TaskEvent;
import com.donglaistd.jinli.processors.handler.MessageHandler;
import com.donglaistd.jinli.service.EventPublisher;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static com.donglaistd.jinli.Constant.ResultCode.DIARY_NOTFOUND;
import static com.donglaistd.jinli.Constant.ResultCode.SUCCESS;
import static com.donglaistd.jinli.util.MessageUtil.buildReply;

@Component
public class StarDiaryRequestHandler extends MessageHandler {
    @Autowired
    DiaryStarDaoService diaryStarDaoService;
    @Autowired
    PersonDiaryDaoService diaryDaoService;
    @Override
    public Jinli.JinliMessageReply doHandle(ChannelHandlerContext ctx, Jinli.JinliMessageRequest messageRequest, User user) {
        var request = messageRequest.getStarDiaryRequest();
        var reply = Jinli.StarDiaryRequestReply.newBuilder();
        PersonDiary diary = diaryDaoService.findById(request.getDiaryId());
        if (Objects.isNull(diary)) {
            return buildReply(reply,DIARY_NOTFOUND);
        }
        DiaryStar starRecord = diaryStarDaoService.findByDiaryIdAndUserId(diary.getId(), user.getId());
        boolean isStar = Objects.isNull(starRecord);
        if(isStar){
            diaryStarDaoService.save(DiaryStar.newInstance(diary.getId(), user.getId()));
            EventPublisher.publish(TaskEvent.newInstance(user.getId(), ConditionType.like,1));
        }else{
            diaryStarDaoService.delete(starRecord);
        }
        reply.setStartNum(diaryStarDaoService.countDiaryStarNum(diary.getId())).setIsStar(isStar);
        return buildReply(reply,SUCCESS);
    }
}
