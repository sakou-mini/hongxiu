package com.donglaistd.jinli.processors.handler.zone;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.dao.PersonDiaryDaoService;
import com.donglaistd.jinli.database.dao.ZoneDaoService;
import com.donglaistd.jinli.database.entity.zone.PersonDiary;
import com.donglaistd.jinli.database.entity.zone.Zone;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class DeleteDiaryRequestHandlerTest extends ZoneBaseTest {

    @Autowired
    DeleteDiaryRequestHandler deleteDiaryRequestHandler;
    @Autowired
    ZoneDaoService zoneDaoService;
    @Autowired
    PersonDiaryDaoService personDiaryDaoService;

    @Test
    public void DeleteDiaryRequestTest() {
        PersonDiary diary = createPassDiary(user);
        Jinli.JinliMessageRequest.Builder builder = Jinli.JinliMessageRequest.newBuilder();
        Jinli.JinliMessageRequest.Builder request = builder.setDeleteDiaryRequest(Jinli.DeleteDiaryRequest.newBuilder().setDiaryId(diary.getId()));
        Jinli.JinliMessageReply handle = deleteDiaryRequestHandler.handle(context, request.build());
        Assert.assertEquals(Constant.ResultCode.SUCCESS, handle.getResultCode());
        PersonDiary diaryDeleteAfter = personDiaryDaoService.findById(diary.getId());
        Assert.assertNull(diaryDeleteAfter);
        Assert.assertEquals(0, personDiaryDaoService.countUserNotFailDiaryNum(user.getId()));
    }
}
