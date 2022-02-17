package com.donglaistd.jinli.processors.handler;

import com.donglaistd.jinli.BaseTest;
import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.RoomManagement;
import com.donglaistd.jinli.database.dao.ReportDaoService;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.accusation.Report;
import com.donglaistd.jinli.processors.handler.connectLive.ReportRequestHandler;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static com.donglaistd.jinli.Constant.ResultCode.ILLEGAL_OPERATION;
import static com.donglaistd.jinli.Constant.ResultCode.SUCCESS;


@RunWith(SpringRunner.class)
@SpringBootTest

public class ReportRequestHandlerTest extends BaseTest {
    @Autowired
    private ReportRequestHandler reportRequestHandler;
    @Autowired
    private ReportDaoService reportDaoService;
    @Test
    public void testReport() {
        liveUser.setUserId(user.getId());
        user.setLiveUserId(liveUser.getId());
        liveUser.setRoomId(room.getId());
        room.setLiveUserId(liveUser.getId());

        User audience = userBuilder.createUser("test_ audience", "audience", "audience");

        dataManager.saveUser(user);
        dataManager.saveLiveUser(liveUser);
        room.addAudience(user);
        dataManager.saveRoom(room);
        RoomManagement.RoomManagementRequest.Builder builder = RoomManagement.RoomManagementRequest.newBuilder();
        RoomManagement.ReportRequest.Builder request = RoomManagement.ReportRequest.newBuilder();
        request.setType(Constant.ViolationType.OTHERS).setContent("你好你好,我就是要举报!").setContact("110");

        var pair = reportRequestHandler.handle(context, builder.setReportRequest(request.build()).build(), user, room);
        Assert.assertEquals(ILLEGAL_OPERATION, pair.getRight());

        pair = reportRequestHandler.handle(context, builder.setReportRequest(request.build()).build(), audience, room);
        Assert.assertEquals(SUCCESS, pair.getRight());
        RoomManagement.ReportReply reply = pair.getLeft().getReportReply();
        Assert.assertNotNull(reply.getReportId());
        Report report = reportDaoService.findById(reply.getReportId());
        Assert.assertNotNull(report);
    }
}
