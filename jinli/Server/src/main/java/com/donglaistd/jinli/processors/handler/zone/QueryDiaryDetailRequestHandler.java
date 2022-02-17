package com.donglaistd.jinli.processors.handler.zone;

import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.dao.*;
import com.donglaistd.jinli.database.entity.FollowList;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.zone.PersonDiary;
import com.donglaistd.jinli.processors.handler.MessageHandler;
import com.donglaistd.jinli.service.DiaryProcess;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static com.donglaistd.jinli.Constant.ResultCode.DIARY_NOTFOUND;
import static com.donglaistd.jinli.Constant.ResultCode.SUCCESS;
import static com.donglaistd.jinli.util.MessageUtil.buildReply;

@Component
public class QueryDiaryDetailRequestHandler extends MessageHandler {
    @Autowired
    UserDaoService userDaoService;
    @Autowired
    DiaryReplyDaoService diaryReplyDaoService;
    @Autowired
    FollowListDaoService followListDaoService;
    @Autowired
    DiaryStarDaoService diaryStarDaoService;
    @Autowired
    PersonDiaryDaoService diaryDaoService;
    @Autowired
    DiaryProcess diaryProcess;
    @Override
    public Jinli.JinliMessageReply doHandle(ChannelHandlerContext ctx, Jinli.JinliMessageRequest messageRequest, User user) {
        Jinli.QueryDiaryDetailRequest request = messageRequest.getQueryDiaryDetailRequest();
        Jinli.QueryDiaryDetailReply.Builder reply = Jinli.QueryDiaryDetailReply.newBuilder();
        String diaryId = request.getDiaryId();
        PersonDiary diary = diaryDaoService.findById(diaryId);
        if (Objects.isNull(diary)) return buildReply(reply,DIARY_NOTFOUND);
        diary.addPlayNumber();
        User diaryUser = userDaoService.findById(diary.getUserId());
        reply.setUserInfo(diaryUser.toSummaryProto()).setDiary(diaryProcess.getDiaryProto(diary).toBuilder()
                .setStarNumber(diaryStarDaoService.countDiaryStarNum(diaryId)))
                .setIsStar(diaryStarDaoService.isStar(diaryId,user.getId()));
        if (Objects.equals(user.getId(), diary.getUserId())) {
            diary.setCommentFlag(false);
        }
        reply.setReplyNum(diaryReplyDaoService.queryReplyNum(diaryId));
        if(!Objects.equals(user.getId(),diary.getUserId())){
            FollowList follower = followListDaoService.findByFollowerIdAndFollower(diaryUser.getLiveUserId(), user.getId());
            reply.setIsFollow(Objects.nonNull(follower));
        }
        diaryDaoService.save(diary);
        return buildReply(reply,SUCCESS);
    }
}
