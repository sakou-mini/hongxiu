package com.donglaistd.jinli.processors.handler;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.dao.FollowListDaoService;
import com.donglaistd.jinli.database.dao.LiveUserDaoService;
import com.donglaistd.jinli.database.dao.RoomDaoService;
import com.donglaistd.jinli.database.dao.UserDaoService;
import com.donglaistd.jinli.database.entity.FollowList;
import com.donglaistd.jinli.database.entity.LiveUser;
import com.donglaistd.jinli.database.entity.Room;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.game.CardGame;
import com.donglaistd.jinli.service.LiveProcess;
import com.donglaistd.jinli.util.DataManager;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.donglaistd.jinli.util.MessageUtil.GenerateLiveUserInfo;
import static com.donglaistd.jinli.util.MessageUtil.buildReply;

@Component
public class FindAllFolloweesRequestHandler extends MessageHandler {

    @Autowired
    private FollowListDaoService followListDaoService;
    @Autowired
    LiveUserDaoService liveUserDaoService;
    @Autowired
    UserDaoService userDaoService;
    @Autowired
    RoomDaoService roomDaoService;
    @Value("${data.rank.coefficient}")
    private int COEFFICIENT;
    @Autowired
    DataManager dataManager;
    @Autowired
    LiveProcess liveProcess;

    @Override
    public Jinli.JinliMessageReply doHandle(ChannelHandlerContext ctx, Jinli.JinliMessageRequest messageRequest, User user) {
        var reply = Jinli.FindAllFolloweesReply.newBuilder();
        var followList = followListDaoService.findAllByFollower(user);
        var followeeList = followList.stream().map(FollowList::getFollowee).collect(Collectors.toSet());
        //推荐的主播
        List<LiveUser> recommendList = liveProcess.getRecommendLiveUserListByUser(user,followeeList);
        var recommendRoom = recommendList.stream().map(this::getRoomInfoByLiveUser).filter(Objects::nonNull).collect(Collectors.toList());
        //已关注主播的直播间
        List<Jinli.RoomInfo> followRooms = new ArrayList<>();
        //已关注的且未开播的主播
        List<Jinli.LiveUserInfo> followLiveUserInfoList = new ArrayList<>();
        Room room;
        User userFromLiveUser;
        for (LiveUser liveUser : followeeList) {
            if (liveUser.getLiveStatus() == Constant.LiveStatus.ONLINE) {
                room = DataManager.findOnlineRoom(liveUser.getRoomId());
                if(Objects.isNull(room) || !room.isSharedToPlatform(user.getPlatformType())) continue;
                Jinli.RoomInfo roomMessage = getRoomInfoByLiveUser(dataManager.findLiveUser(liveUser.getId()));
                if(Objects.nonNull(roomMessage)) followRooms.add(roomMessage);
            } else if (liveUser.getLiveStatus() == Constant.LiveStatus.OFFLINE) {
                liveUser = liveUserDaoService.findById(liveUser.getId());
                room = roomDaoService.findByLiveUser(liveUser);
                userFromLiveUser = userDaoService.findByLiveUserId(liveUser.getId());
                followLiveUserInfoList.add(GenerateLiveUserInfo(liveUser, userFromLiveUser, room));
            }
        }
        reply.addAllRoomList(followRooms).addAllRecommended(recommendRoom).addAllLiveUserInfo(followLiveUserInfoList);
        return buildReply(reply, resultCode);
    }

    private Jinli.RoomInfo getRoomInfoByLiveUser(LiveUser liveUser) {
        var room = DataManager.findOnlineRoom(liveUser.getRoomId());
        CardGame game = DataManager.findGame(liveUser.getPlayingGameId());
        if(Objects.isNull(room) || Objects.isNull(game)){
            liveUser.cleanLive();
            dataManager.saveLiveUser(liveUser);
            return null;
        }
        var roomInfoBuilder = Jinli.RoomInfo.newBuilder();
        int fansNum = followListDaoService.fetchHotValueByLiveUser(liveUser);
        int hotValue = room.getAllPlatformAudienceList().size();
        Constant.GameType gameType = game.getGameType();
        var user = userDaoService.findById(liveUser.getUserId());
        Jinli.LiveUserInfo liveUserInfo = GenerateLiveUserInfo(liveUser, user, fansNum);
        roomInfoBuilder.setId(room.getId()).setRoomTitle(room.getRoomTitle()).setDescription(room.getDescription())
                .setRoomImage(room.getRoomImage()).setHotValue(hotValue * COEFFICIENT)
                .setGameType(gameType).setPattern(room.getPattern()).setTotalAmount(room.getCoinTotalCount()).setLiveUserInfo(liveUserInfo).setUserId(liveUser.getUserId());
        return roomInfoBuilder.build();
    }
}
