package com.donglaistd.jinli.processors.handler.music;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.dao.MusicListDaoService;
import com.donglaistd.jinli.database.entity.LiveUser;
import com.donglaistd.jinli.database.entity.MusicList;
import com.donglaistd.jinli.database.entity.Room;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.processors.handler.MessageHandler;
import com.donglaistd.jinli.util.DataManager;
import com.donglaistd.jinli.util.StringUtils;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

import static com.donglaistd.jinli.util.MessageUtil.buildReply;
//TODO (DELETE)
@Component
public class AdjustBackgroundMusicVolumeRequestHandler extends MessageHandler {
    @Autowired
    MusicListDaoService musicListDaoService;

    @Override
    protected Jinli.JinliMessageReply doHandle(ChannelHandlerContext ctx, Jinli.JinliMessageRequest messageRequest, User user) {
        Jinli.AdjustBackgroundMusicVolumeRequest volumeRequest = messageRequest.getAdjustBackgroundMusicVolumeRequest();
        Jinli.AdjustBackgroundMusicVolumeReply.Builder reply = Jinli.AdjustBackgroundMusicVolumeReply.newBuilder();
        if(volumeRequest.getVolume() > 100 || volumeRequest.getVolume() <0 )
            return buildReply(reply, Constant.ResultCode.PARAM_ERROR);
        if(StringUtils.isNullOrBlank(user.getLiveUserId()) ||  Objects.isNull(dataManager.findLiveUser(user.getLiveUserId()))) {
            return buildReply(reply, Constant.ResultCode.NOT_LIVE_USER);
        }
        LiveUser liveUser = dataManager.findLiveUser(user.getLiveUserId());
        Room onlineRoom = DataManager.findOnlineRoom(liveUser.getRoomId());
        if(Objects.isNull(onlineRoom)){
            return buildReply(reply, Constant.ResultCode.ROOM_DOES_NOT_EXIST);
        }
        MusicList musicList = musicListDaoService.findByUserId(user.getId());
        musicList.setVolume(volumeRequest.getVolume());
        musicListDaoService.save(musicList);
        broadcastBackgroundMusicVolume(musicList.getVolume(), onlineRoom);
        return buildReply(reply, Constant.ResultCode.SUCCESS);
    }

    public void broadcastBackgroundMusicVolume(int volume,Room room){
        Jinli.BackgroundMusicVolumeBroadcastMessage.Builder musicVolumeMessage = Jinli.BackgroundMusicVolumeBroadcastMessage.newBuilder().setVolume(volume);
        room.broadCastToAllPlatform(Jinli.JinliMessageReply.newBuilder().setBackgroundMusicVolumeBroadcastMessage(musicVolumeMessage).build());
    }
}
