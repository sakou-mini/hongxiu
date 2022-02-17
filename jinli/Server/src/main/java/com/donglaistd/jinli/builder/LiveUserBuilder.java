package com.donglaistd.jinli.builder;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.dao.LiveUserDaoService;
import com.donglaistd.jinli.database.dao.RoomDaoService;
import com.donglaistd.jinli.database.entity.LiveUser;
import com.donglaistd.jinli.database.entity.Room;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.util.DataManager;
import com.donglaistd.jinli.util.IdGeneratedProcess;
import com.donglaistd.jinli.util.Utils;
import com.donglaistd.jinli.util.VerifyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.Objects;
import java.util.logging.Logger;

import static com.donglaistd.jinli.constant.GameConstant.DEFAULT_ROOM_IMAGE_PATH;
import static com.donglaistd.jinli.constant.GameConstant.LIVE_USER_ID_LENGTH;

@Component
public class LiveUserBuilder {
    private  static final Logger logger = Logger.getLogger(LiveUserBuilder.class.getName());
    @Autowired
    LiveUserDaoService liveUserDaoService;
    @Autowired
    RoomDaoService roomDaoService;
    @Autowired
    DataManager dataManager;
    @Autowired
    RoomBuilder roomBuilder;
    @Autowired
    VerifyUtil verifyUtil;
    @Autowired
    IdGeneratedProcess idGeneratedProcess;

    @Transactional
    public synchronized LiveUser create(String userId, Constant.LiveStatus liveStatus,Constant.PlatformType platform) {
        LiveUser liveUser = liveUserDaoService.findByUserId(userId);
        if(Objects.isNull(liveUser)){
            String id = idGeneratedProcess.generatedId(LiveUser.class.getSimpleName(), LIVE_USER_ID_LENGTH);
            liveUser = new LiveUser(id, userId, liveStatus, Utils.makeLiveToken(), platform);
        }
        return liveUserDaoService.save(liveUser);
    }

    public LiveUser becomeLiveUserByUserAndStatue(User user, Constant.LiveStatus liveStatus, Constant.PlatformType platformType) {
        if(verifyUtil.checkIsLiveUser(user)) {
            logger.info("already is liveUser");
            return null;
        }
        LiveUser liveUser = liveUserDaoService.findByUserId(user.getId());
        if(Objects.isNull(liveUser))
            liveUser = create(user.getId(), liveStatus,platformType);

        Room room = roomDaoService.findByLiveUser(liveUser);
        if(Objects.isNull(room)) {
            room = roomBuilder.create(liveUser.getId(), user.getId(),"", "",DEFAULT_ROOM_IMAGE_PATH);
            liveUser.setRoomId(room.getId());
        }
        liveUser.setLiveStatus(liveStatus);
        liveUser.setPlatformType(platformType);

        //user platform only set by LiveStatus is offline or online
        if(VerifyUtil.isOfficialLiveStatue(liveStatus)) {
            user.setPlatformType(platformType);
            user.setLiveUserId(liveUser.getId());
            //clean user coin if is other platform LiveUser
            if(user.isPlatformUser()) user.setGameCoin(0);
            liveUser.setAuditTime(System.currentTimeMillis());
        }

        dataManager.saveUser(user);
        dataManager.saveLiveUser(liveUser);
        roomDaoService.save(room);
        return liveUser;
    }
}
