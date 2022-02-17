package com.donglai.web.process;

import com.donglai.common.constant.UserStatus;
import com.donglai.common.util.PasswordUtil;
import com.donglai.common.util.StringUtils;
import com.donglai.common.util.TimeUtil;
import com.donglai.model.db.entity.common.User;
import com.donglai.model.db.entity.live.LiveRecord;
import com.donglai.model.db.entity.live.LiveUser;
import com.donglai.model.db.entity.live.Room;
import com.donglai.model.db.service.common.UserService;
import com.donglai.model.db.service.live.LiveRecordService;
import com.donglai.model.db.service.live.LiveUserService;
import com.donglai.protocol.Constant;
import com.donglai.web.builder.LiveUserBuilder;
import com.donglai.web.builder.RoomBuilder;
import com.donglai.web.db.server.service.LiveUserDbService;
import com.donglai.web.response.GlobalResponseCode;
import com.donglai.web.response.PageInfo;
import com.donglai.web.web.dto.reply.LiveUserDetailDto;
import com.donglai.web.web.dto.reply.LiveUserListReply;
import com.donglai.web.web.dto.request.LiveUserListRequest;
import com.donglai.web.web.dto.request.UpdateUserStatusRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class LiveUserProcess {
    @Autowired
    UserService userService;
    @Autowired
    LiveUserService liveUserService;
    @Autowired
    LiveUserBuilder liveUserBuilder;
    @Autowired
    RoomBuilder roomBuilder;
    @Autowired
    LiveUserDbService liveUserDbService;
    @Autowired
    LiveRecordService liveRecordService;

    /*1.申请主播*/
    public void becomeSimpleLiveUser(User user, Constant.PlatformType platform) {
        LiveUser liveUser = liveUserBuilder.createSimpleLiveUser(user.getId(), Constant.LiveUserStatus.LIVE_OFFLINE, platform);
        Room room = roomBuilder.createRoom(liveUser.getId(), user.getId());
        liveUser.setRoomId(room.getId());
        user.becomeLiveUser(liveUser.getId());
        liveUserService.save(liveUser);
        userService.save(user);
    }

    /*2.查询主播列表*/
    public PageInfo<LiveUserListReply> liveUserList(LiveUserListRequest request){
        PageInfo<LiveUser> pageResult = liveUserDbService.liveUserList(request);
        List<LiveUserListReply> content = pageResult.getContent().stream().map(this::buildLiveUserListReply).collect(Collectors.toList());
        return new PageInfo<>(pageResult, content);
    }

    private LiveUserListReply buildLiveUserListReply(LiveUser liveUser){
        User user = userService.findById(liveUser.getUserId());
        LiveUserListReply liveUserListReply = new LiveUserListReply(user);
        //TODO 设置月在线时常
        return liveUserListReply;
    }

    /*3.查询主播详情*/
    public LiveUserDetailDto getLiveUserDetail(String userId) {
        if(StringUtils.isNullOrBlank(userId)) return null;
        User user = userService.findById(userId);
        if(Objects.isNull(user)) return null;
        //性能堪忧需要优化。
        //总直播记录
        List<LiveRecord> allLiveRecord = liveRecordService.findByUserIdAndTimes(userId, null, null);
        //月直播记录
        List<LiveRecord> monthLiveRecord = liveRecordService.findByUserIdAndTimes(userId, TimeUtil.getBeforeMonthStartTime(1), System.currentTimeMillis());
        //最近直播记录
        LiveRecord lastLiveRecord = CollectionUtils.isEmpty(allLiveRecord) ? null : allLiveRecord.get(0);
        long allLiveTime = allLiveRecord.stream().mapToLong(LiveRecord::getLiveTime).sum();
        long monthLiveSumTime =  monthLiveRecord.stream().mapToLong(LiveRecord::getLiveTime).sum();
        LiveUserDetailDto liveUserDetailDto = new LiveUserDetailDto(user,monthLiveSumTime,allLiveTime,allLiveRecord.size());
        if(Objects.nonNull(lastLiveRecord)){
            liveUserDetailDto.setLastLiveStartTime(lastLiveRecord.getStartTime());
            liveUserDetailDto.setLastLiveTime(lastLiveRecord.getLiveTime());
        }
        return liveUserDetailDto;
    }

    public GlobalResponseCode updateUserStatus(UpdateUserStatusRequest request) {
        if(!Objects.equals(UserStatus.NORMAL.getValue(),request.getStatus()) && !Objects.equals(UserStatus.BAN.getValue(),request.getStatus()))
            return GlobalResponseCode.PARAM_ERROR;
        List<String> userIds = request.getUserIds();
        List<User> users = userService.findByIds(request.getUserIds());
        if(!Objects.equals(users.size(),request.getUserIds().size()))
            return GlobalResponseCode.USER_NOT_FOUND;
        users.forEach(user -> user.setStatus(request.getStatus()));
        userService.saveAll(users);
        return GlobalResponseCode.SUCCESS;
    }

    public GlobalResponseCode updatePassword(String userId, String password) {
        User user = userService.findById(userId);
        if(Objects.isNull(user)) return GlobalResponseCode.USER_NOT_FOUND;
        else if(StringUtils.isNullOrBlank(password) || password.length() < 8 || password.length() >16) return GlobalResponseCode.PASSWORD_ILLEGALITY;
        user.setPassword(PasswordUtil.encodePassword(password));
        userService.save(user);
        return GlobalResponseCode.SUCCESS;
    }
}
