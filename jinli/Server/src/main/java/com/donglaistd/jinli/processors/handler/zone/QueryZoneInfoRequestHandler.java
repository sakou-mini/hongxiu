package com.donglaistd.jinli.processors.handler.zone;

import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.dao.*;
import com.donglaistd.jinli.database.entity.LiveUser;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.zone.PersonDiary;
import com.donglaistd.jinli.database.entity.zone.Zone;
import com.donglaistd.jinli.processors.handler.MessageHandler;
import com.donglaistd.jinli.service.DiaryProcess;
import com.donglaistd.jinli.util.DataManager;
import com.donglaistd.jinli.util.VerifyUtil;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.donglaistd.jinli.Constant.DiaryStatue.DIARY_APPROVAL_PASS;
import static com.donglaistd.jinli.Constant.ResultCode.*;
import static com.donglaistd.jinli.util.MessageUtil.buildReply;

@Component
public class QueryZoneInfoRequestHandler extends MessageHandler {
    @Autowired
    ZoneDaoService zoneDaoService;
    @Autowired
    PersonDiaryDaoService personDiaryDaoService;
    @Autowired
    DataManager dataManager;
    @Autowired
    VerifyUtil verifyUtil;
    @Autowired
    private FollowListDaoService followListDaoService;
    @Autowired
    DiaryStarDaoService diaryStarDaoService;
    @Autowired
    DiaryProcess diaryProcess;
    @Autowired
    UserDaoService userDaoService;

    @Override
    public Jinli.JinliMessageReply doHandle(ChannelHandlerContext ctx, Jinli.JinliMessageRequest messageRequest, User user) {
        Jinli.QueryZoneInfoRequest request = messageRequest.getQueryZoneInfoRequest();
        Jinli.QueryZoneInfoReply.Builder reply = Jinli.QueryZoneInfoReply.newBuilder();
        String queryUserId = request.getUserId();
        User queryUser = userDaoService.findById(queryUserId);
        if (Objects.isNull(queryUser)) return buildReply(reply,USER_NOT_FOUND);

        LiveUser liveUser = dataManager.findLiveUser(queryUser.getLiveUserId());
        if (!verifyUtil.verifyIsLiveUser(liveUser)) return buildReply(reply, NOT_LIVE_USER);

        Zone zone = zoneDaoService.findOrCreateZoneByUserId(queryUserId);
        reply.setUserInfo(queryUser.toSummaryProto()).setLiveUserInfo(buildLiveUserInfo(liveUser)).setZone(zone.toProto());
        statisticsZoneDiaryInfo(zone, user, reply);
        List<String> followeeIds = followListDaoService.findFolloweeIdsByFollowerId(queryUser.getLiveUserId());
        reply.setFansNum(followeeIds.size());
        if (!Objects.equals(user.getId(), queryUserId)) {
            reply.setIsFollow(followeeIds.contains(user.getId()));
        }
        return buildReply(reply, SUCCESS);
    }

    private Jinli.LiveUserInfo buildLiveUserInfo(LiveUser liveUser) {
        return Jinli.LiveUserInfo.newBuilder().setLevel(liveUser.getLevel()).setGender(liveUser.getGender()).setStatus(liveUser.getLiveStatus()).build();
    }

    private void statisticsZoneDiaryInfo(Zone zone, User currentUser, Jinli.QueryZoneInfoReply.Builder reply) {
        List<PersonDiary> diaries;
        boolean ownerFlag;
        if (Objects.equals(zone.getUserId(), currentUser.getId())) {
            diaries = personDiaryDaoService.findUserNotFailDiary(zone.getUserId());
            ownerFlag = true;
        } else {
            diaries = personDiaryDaoService.findDiaryByUserIdAndStatue(zone.getUserId(),DIARY_APPROVAL_PASS);
            ownerFlag = false;
        }
        long totalStars = 0;
        long totalPlay = 0;
        List<Jinli.Diary> diaryList = new ArrayList<>();
        Jinli.Diary diaryProto;
        for (PersonDiary diary : diaries) {
            totalStars += diaryStarDaoService.countDiaryStarNum(diary.getId());
            totalPlay += diary.getPlayNumber();
            diaryProto =  diaryProcess.getDiarySummaryProto(diary).toBuilder().setStarNumber(diaryStarDaoService.countDiaryStarNum(diary.getId())).build();
            if (ownerFlag) {
                diaryProto = diaryProto.toBuilder().setCommentFlag(diary.getCommentFlag()).build();
            }
            diaryList.add(diaryProto);
        }
        reply.setStarNum(totalStars).setPlayNum(totalPlay).addAllDiary(diaryList).getZoneBuilder().setDiaryNum(diaries.size());
    }

}
