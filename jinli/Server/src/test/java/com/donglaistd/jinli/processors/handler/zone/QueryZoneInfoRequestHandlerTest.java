package com.donglaistd.jinli.processors.handler.zone;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.dao.PersonDiaryDaoService;
import com.donglaistd.jinli.database.entity.zone.PersonDiary;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.donglaistd.jinli.Constant.DiaryStatue.DIARY_APPROVAL_PASS;
import static com.donglaistd.jinli.Constant.DiaryStatue.DIARY_UNAPPROVED;

public class QueryZoneInfoRequestHandlerTest extends ZoneBaseTest {
    @Autowired
    QueryZoneInfoRequestHandler queryZoneInfoRequestHandler;

    @Autowired
    PersonDiaryDaoService personDiaryDaoService;

    @Test
    public void createPersonDiaryTest(){
        PersonDiary diary1 = createPersonDiary(user,  Constant.DiaryType.IMAGE,"1",DIARY_APPROVAL_PASS);
        PersonDiary diary2 = createPersonDiary(user,  Constant.DiaryType.VIDEO,"2",DIARY_APPROVAL_PASS);
        PersonDiary diary3 = createPersonDiary(user,  Constant.DiaryType.IMAGE,"3", Constant.DiaryStatue.DIARY_UPLOADING);
        PersonDiary diary4 = createPersonDiary(user,  Constant.DiaryType.VIDEO,"4",DIARY_UNAPPROVED);
        PersonDiary diary5 = createPersonDiary(user,  Constant.DiaryType.IMAGE,"5",DIARY_APPROVAL_PASS);

        List<PersonDiary> userDiaries = personDiaryDaoService.findUserNotFailDiary(user.getId());
        Assert.assertEquals(4, userDiaries.size());
        Assert.assertFalse(userDiaries.contains(diary3));
        Jinli.JinliMessageRequest.Builder builder = Jinli.JinliMessageRequest.newBuilder();
        Jinli.JinliMessageRequest.Builder request = builder.setQueryZoneInfoRequest(Jinli.QueryZoneInfoRequest.newBuilder().setUserId(user.getId()));
        Jinli.JinliMessageReply resultBuilder = queryZoneInfoRequestHandler.handle(context, request.build());
        Jinli.QueryZoneInfoReply reply = resultBuilder.getQueryZoneInfoReply();
        Assert.assertEquals(0,reply.getPlayNum());
        Assert.assertEquals(0,reply.getStarNum());
        Assert.assertEquals(4,reply.getDiaryCount());
    }

}
