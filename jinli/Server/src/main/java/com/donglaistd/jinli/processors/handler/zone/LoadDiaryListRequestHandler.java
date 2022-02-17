package com.donglaistd.jinli.processors.handler.zone;

import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.dao.PersonDiaryDaoService;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.zone.PersonDiary;
import com.donglaistd.jinli.processors.handler.MessageHandler;
import com.donglaistd.jinli.service.DiaryProcess;
import com.donglaistd.jinli.util.DataManager;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Set;

import static com.donglaistd.jinli.Constant.ResultCode.SUCCESS;
import static com.donglaistd.jinli.util.MessageUtil.buildReply;

@Component
public class LoadDiaryListRequestHandler  extends MessageHandler {
    @Autowired
    RedisTemplate<String, String> stringRedisTemplate;

    @Autowired
    DataManager dataManager;
    @Autowired
    PersonDiaryDaoService personDiaryDaoService;
    @Autowired
    DiaryProcess diaryProcess;

    @Override
    public Jinli.JinliMessageReply doHandle(ChannelHandlerContext ctx, Jinli.JinliMessageRequest messageRequest, User user) {
        Jinli.LoadDiaryListRequest request = messageRequest.getLoadDiaryListRequest();
        Jinli.LoadDiaryListReply.Builder reply = Jinli.LoadDiaryListReply.newBuilder();
        int num = request.getNum();
        Set<String> ids = diaryProcess.randomDiary(user.getId(),num);
        PersonDiary diary;
        for (String id : ids) {
            diary = personDiaryDaoService.findById(id);
            if(Objects.nonNull(diary)){
                reply.addDiary(diaryProcess.getDiarySummaryProto(diary));
            }
        }
        return buildReply(reply, SUCCESS);
    }

}
