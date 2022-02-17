package com.donglaistd.jinli.http.service;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.dao.RoomDaoService;
import com.donglaistd.jinli.database.entity.LiveUser;
import com.donglaistd.jinli.database.entity.Room;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.http.entity.UploadFileInfo;
import com.donglaistd.jinli.util.DataManager;
import com.donglaistd.jinli.util.HttpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.donglaistd.jinli.Constant.ResultCode.SUCCESS;

@Component
public class UpdateRoomImageUploadService extends RequestUploadService{
    @Value("${data.live-room.cover.max_width}")
    private int limitWidth;
    @Value("${data.live-room.cover.max_height}")
    private int limitHeight;
    @Autowired
    private DataManager dataManager;
    @Autowired
    private RoomDaoService roomDaoService;
    @Autowired
    private HttpUtil httpUtil;

    @Override
    public Constant.ResultCode handle(UploadFileInfo uploadFileInfo, User user) {
        String roomImagePath = uploadFileInfo.getFile_path().get(0);
       /* Constant.ResultCode resultCode = httpUtil.verifyImageSize(roomImagePath, uploadFileInfo.getRemoteAddr(), limitWidth, limitHeight);
        if(!SUCCESS.equals(resultCode)) return resultCode;*/
        LiveUser onlineLiveUser = dataManager.findLiveUser(user.getLiveUserId());
        Room room = DataManager.findOnlineRoom(onlineLiveUser.getRoomId());
        if (room != null) {
            room.setRoomImage(roomImagePath);
            dataManager.saveRoom(room);
        } else {
            Optional.ofNullable(roomDaoService.findByLiveUser(onlineLiveUser)).ifPresent(r -> {
                r.setRoomImage(roomImagePath);
                roomDaoService.save(r);
            });
        }
        return Constant.ResultCode.SUCCESS;
    }
}
