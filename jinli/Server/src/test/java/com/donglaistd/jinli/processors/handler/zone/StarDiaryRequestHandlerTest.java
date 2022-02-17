package com.donglaistd.jinli.processors.handler.zone;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.dao.PersonDiaryDaoService;
import com.donglaistd.jinli.database.dao.ZoneDaoService;
import com.donglaistd.jinli.database.entity.zone.PersonDiary;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class StarDiaryRequestHandlerTest extends ZoneBaseTest {
    @Autowired
    StarDiaryRequestHandler starDiaryRequestHandler;
    @Autowired
    ZoneDaoService zoneDaoService;
    @Autowired
    PersonDiaryDaoService personDiaryDaoService;

    @Test
    public void starDiaryRequestTest(){
        PersonDiary diary = createPassDiary(user);
        Jinli.JinliMessageRequest.Builder builder = Jinli.JinliMessageRequest.newBuilder();
        Jinli.JinliMessageRequest.Builder request = builder.setStarDiaryRequest(Jinli.StarDiaryRequest.newBuilder().setDiaryId(diary.getId()));
        Jinli.JinliMessageReply handle = starDiaryRequestHandler.handle(context, request.build());
        Assert.assertEquals(Constant.ResultCode.SUCCESS,handle.getResultCode());
        Assert.assertEquals(1,handle.getStarDiaryRequestReply().getStartNum());
        Assert.assertTrue(handle.getStarDiaryRequestReply().getIsStar());
    }

}
