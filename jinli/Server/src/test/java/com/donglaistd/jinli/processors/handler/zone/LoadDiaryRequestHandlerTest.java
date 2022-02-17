package com.donglaistd.jinli.processors.handler.zone;

import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.dao.PersonDiaryDaoService;
import com.donglaistd.jinli.database.dao.ZoneDaoService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class LoadDiaryRequestHandlerTest extends ZoneBaseTest {
    @Autowired
    ZoneDaoService zoneDaoService;
    @Autowired
    PersonDiaryDaoService personDiaryDaoService;
    @Autowired
    LoadDiaryListRequestHandler loadDiaryListRequestHandler;

    @Test
    public void loadDiaryListTest(){
        initDiaryData(3,user);
        Jinli.JinliMessageRequest.Builder builder = Jinli.JinliMessageRequest.newBuilder();
        Jinli.JinliMessageRequest.Builder request = builder.setLoadDiaryListRequest(Jinli.LoadDiaryListRequest.newBuilder().setNum(3));
        Jinli.JinliMessageReply handle = loadDiaryListRequestHandler.handle(context, request.build());
        Jinli.LoadDiaryListReply reply = handle.getLoadDiaryListReply();
        Assert.assertEquals(3, reply.getDiaryList().size());
    }
}
