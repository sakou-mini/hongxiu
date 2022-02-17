package com.donglaistd.jinli.processors.handler.connectLive;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.RoomManagement;
import com.donglaistd.jinli.database.dao.ReportDaoService;
import com.donglaistd.jinli.database.dao.UserDaoService;
import com.donglaistd.jinli.database.entity.LiveUser;
import com.donglaistd.jinli.database.entity.Room;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.accusation.Report;
import com.donglaistd.jinli.util.DataManager;
import com.donglaistd.jinli.util.Pair;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

import static com.donglaistd.jinli.Constant.ResultCode.*;

@Component
public class ReportRequestHandler extends RoomManagementHandler {
    @Autowired
    private ReportDaoService reportDaoService;
    @Autowired
    private DataManager dataManager;
    @Autowired
    UserDaoService userDaoService;
    @Value("${data.report.content.min}")
    private int MIN_LENGTH;
    @Value("${data.report.content.max}")
    private int MAX_LENGTH;

    @Override
    public Pair<RoomManagement.RoomManagementReply, Constant.ResultCode> handle(ChannelHandlerContext ctx, RoomManagement.RoomManagementRequest messageRequest, User user, Room room) {
        RoomManagement.ReportReply.Builder builder = RoomManagement.ReportReply.newBuilder();
        RoomManagement.ReportRequest request = messageRequest.getReportRequest();
        Constant.ViolationType type = request.getType();
        String content = request.getContent();
        String contact = request.getContact();
        if (content.length() > MAX_LENGTH || content.length() < MIN_LENGTH) {
            return generateReply(builder, ILLEGAL_PARAMETER_LENGTH);
        }
        LiveUser liveUser = dataManager.findLiveUser(room.getLiveUserId());
        User beReportUser = userDaoService.findById(liveUser.getUserId());
        if (Objects.isNull(beReportUser)) {
            return generateReply(builder, USER_NOT_FOUND);
        }
        else if (liveUser.containsDisablePermission(Constant.LiveUserPermission.PERMISSION_REPORT)) {
            return generateReply(builder, PERMISSION_DISABLED);
        }
        else if (Objects.equals(user.getId(), beReportUser.getId())) {
            return generateReply(builder, ILLEGAL_OPERATION);
        }
        Report instance = Report.newInstance(user.getId(), user.getDisplayName(), contact, beReportUser.getId(), room.getDisplayId(), beReportUser.getDisplayName(), type, content);
        reportDaoService.save(instance);
        return generateReply(builder.setReportId(instance.getId()), SUCCESS);
    }

    private Pair<RoomManagement.RoomManagementReply, Constant.ResultCode> generateReply(RoomManagement.ReportReply.Builder builder, Constant.ResultCode resultCode) {
        RoomManagement.RoomManagementReply.Builder reply = RoomManagement.RoomManagementReply.newBuilder();
        reply.setReportReply(builder.build());
        return new Pair<>(reply.build(), resultCode);
    }
}
