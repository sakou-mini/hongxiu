package com.donglaistd.jinli.processors.handler;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.dao.RoomDaoService;
import com.donglaistd.jinli.database.dao.UserDaoService;
import com.donglaistd.jinli.database.entity.LiveUser;
import com.donglaistd.jinli.database.entity.Room;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.util.DataManager;
import com.google.protobuf.ByteString;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;
import java.util.Optional;

import static com.donglaistd.jinli.Constant.ResultCode.BYTES_IS_EMPTY;
import static com.donglaistd.jinli.util.MessageUtil.buildReply;


@Component
public class UpdateRoomImageRequestHandler extends MessageHandler {
    @Autowired
    private UserDaoService userDaoService;
    @Autowired
    private RoomDaoService roomDaoService;

    @Value("${data.live-room.cover.max_width}")
    private int IMAGE_WIDTH;
    @Value("${data.live-room.cover.max_height}")
    private int IMAGE_HEIGHT;
    @Value("${data.live-room.cover.save_path}")
    private String savePath;

    @Autowired
    DataManager dataManager;

    @Override
    @Transactional
    public Jinli.JinliMessageReply doHandle(ChannelHandlerContext ctx, Jinli.JinliMessageRequest messageRequest, User user) {
        Jinli.UpdateRoomImageRequest request = messageRequest.getUpdateRoomImageRequest();
        Jinli.UpdateRoomImageReply.Builder reply = Jinli.UpdateRoomImageReply.newBuilder();
        ByteString data = request.getCoverData();
        if (Objects.isNull(data) || data.isEmpty()) return buildReply(reply, BYTES_IS_EMPTY);
        String timeString = new SimpleDateFormat("yyyyMMddhhmmss").format(Calendar.getInstance().getTime());
        String roomImageUrl = user.getId() + "/" + timeString;
        resultCode = saveImage(data, savePath, IMAGE_WIDTH, IMAGE_HEIGHT, timeString, user);
        if (Constant.ResultCode.SUCCESS.equals(resultCode)) {
            LiveUser onlineLiveUser = dataManager.findLiveUser(user.getLiveUserId());
            Room room = DataManager.findOnlineRoom(onlineLiveUser.getRoomId());
            if (room != null) {
                room.setRoomImage(roomImageUrl);
                dataManager.saveRoom(room);
            } else {
                Optional.ofNullable(roomDaoService.findByLiveUser(onlineLiveUser)).ifPresent(r -> {
                    r.setRoomImage(roomImageUrl);
                    roomDaoService.save(r);
                });
            }
        }
        userDaoService.save(user);
        return buildReply(reply, resultCode);
    }

}
