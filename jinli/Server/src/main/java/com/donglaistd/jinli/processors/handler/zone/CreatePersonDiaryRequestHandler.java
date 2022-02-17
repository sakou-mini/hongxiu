package com.donglaistd.jinli.processors.handler.zone;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.annotation.IgnoreShutDown;
import com.donglaistd.jinli.config.ZoneConfigProperties;
import com.donglaistd.jinli.database.dao.PersonDiaryDaoService;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.zone.PersonDiary;
import com.donglaistd.jinli.processors.handler.MessageHandler;
import com.donglaistd.jinli.util.DataManager;
import com.donglaistd.jinli.util.StringUtils;
import com.donglaistd.jinli.util.WordFilterUtil;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

import static com.donglaistd.jinli.Constant.DiaryStatue.DIARY_UPLOADING;
import static com.donglaistd.jinli.Constant.DiaryType.IMAGE;
import static com.donglaistd.jinli.Constant.ResultCode.*;
import static com.donglaistd.jinli.util.MessageUtil.buildReply;

@IgnoreShutDown
@Component
public class CreatePersonDiaryRequestHandler extends MessageHandler {
    private static final Logger logger = Logger.getLogger(CreatePersonDiaryRequestHandler.class.getName());
    @Autowired
    PersonDiaryDaoService personDiaryDaoService;
    @Autowired
    ZoneConfigProperties zoneConfigProperties;
    @Autowired
    DataManager dataManager;
    @Autowired
    WordFilterUtil wordFilterUtil;
    @Override
    @Transactional
    public Jinli.JinliMessageReply doHandle(ChannelHandlerContext ctx, Jinli.JinliMessageRequest messageRequest, User user) {
        var request = messageRequest.getCreatePersonDiaryRequest();
        var reply = Jinli.CreatePersonDiaryReply.newBuilder();
        var content = request.getContent();
        int targetNum = request.getTargetNum();
        Constant.DiaryType type = request.getType();
        if (StringUtils.isNullOrBlank(content) || content.length() > zoneConfigProperties.getDiaryContentMaxSize()) return buildReply(reply, CONTENT_IS_EMPTY_OR_TOO_LONG);

        if(wordFilterUtil.containSensitiveWord(content)) return buildReply(reply, CONTENT_WORDS_ILLEGAL);
        long diaryNum = personDiaryDaoService.countUserNotFailDiaryNum(user.getId());
        if (diaryNum >= zoneConfigProperties.getDiaryMaxNum()) return buildReply(reply, DIARY_OVERLIMIT);

        //remove user UnFinishDiary
        List<PersonDiary> unFinishDiary = personDiaryDaoService.findDiaryByUserIdAndStatue(user.getId(),DIARY_UPLOADING);
        if(Objects.nonNull(unFinishDiary)) unFinishDiary.forEach(diary-> personDiaryDaoService.deleteAndCleanDiaryCache(diary));
        if(!verifyTargetResourceNum(targetNum,type)) return buildReply(reply, UPLOADPARAM_OVERLIMIT);

        PersonDiary personDiary = PersonDiary.newInstance(user.getId(), content, type,targetNum);
        personDiaryDaoService.save(personDiary);
        return buildReply(reply.setDiaryId(personDiary.getId()), SUCCESS);
    }

    public boolean verifyTargetResourceNum(int targetNum,Constant.DiaryType type){
        if(Objects.equals(IMAGE,type))
            return targetNum <= zoneConfigProperties.getImageMaxNum();
        else
            return targetNum <= zoneConfigProperties.getVideoMaxNum();
    }
}

