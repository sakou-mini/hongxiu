package com.donglaistd.jinli.processors.handler.music;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.entity.Room;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.processors.handler.MessageHandler;
import com.donglaistd.jinli.util.DataManager;
import com.donglaistd.jinli.util.StringUtils;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.stereotype.Component;

import static com.donglaistd.jinli.constant.GameConstant.BROADCAST_TYPE_LIVE_PLAY_MUSIC;
import static com.donglaistd.jinli.constant.GameConstant.BROADCAST_TYPE_LIVE_STOP_MUSIC;
import static com.donglaistd.jinli.util.MessageUtil.buildReply;

@Component
public class SetRoomPlayingMusicInfoRequestHandler extends MessageHandler {

    @Override
    protected Jinli.JinliMessageReply doHandle(ChannelHandlerContext ctx, Jinli.JinliMessageRequest messageRequest, User user) {
        Jinli.SetRoomPlayingMusicInfoRequest request = messageRequest.getSetRoomPlayingMusicInfoRequest();
        Jinli.SetRoomPlayingMusicInfoReply.Builder replyBuilder = Jinli.SetRoomPlayingMusicInfoReply.newBuilder();
        if(StringUtils.isNullOrBlank(user.getLiveUserId()))
            return buildReply(replyBuilder, Constant.ResultCode.NOT_LIVE_USER);
        Room room = DataManager.findRoomByLiveUserId(user.getLiveUserId());
        if(room == null)
            return buildReply(replyBuilder, Constant.ResultCode.ROOM_DOES_NOT_EXIST);
        Jinli.RoomPlayingMusicChangeBroadcastMessage.Builder broadcastMessage = Jinli.RoomPlayingMusicChangeBroadcastMessage.newBuilder().setMusicOptType(request.getMusicOptType());
        if(!StringUtils.isNullOrBlank(request.getMusic()))
            broadcastMessage.setMusic(request.getMusic());
        room.broadCastToAllPlatform(buildReply(broadcastMessage,null));
        processRoomMusicOptByMusicOptType(room, request);
        return buildReply(replyBuilder, Constant.ResultCode.SUCCESS);
    }

    public void processRoomMusicOptByMusicOptType(Room room,Jinli.SetRoomPlayingMusicInfoRequest request){
        String musicOptType = request.getMusicOptType();
        if(BROADCAST_TYPE_LIVE_PLAY_MUSIC.equals(musicOptType)){
            room.setLiveMusic(request.getMusic());
        }else if(BROADCAST_TYPE_LIVE_STOP_MUSIC.equals(musicOptType)){
            room.setLiveMusic("");
        }
        dataManager.saveRoom(room);
    }
}
