package com.donglaistd.jinli.processors.handler.music;

import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.dao.MusicListDaoService;
import com.donglaistd.jinli.database.entity.MusicList;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.processors.handler.MessageHandler;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.logging.Logger;

import static com.donglaistd.jinli.Constant.ResultCode.SUCCESS;
import static com.donglaistd.jinli.util.MessageUtil.buildReply;

//TODO (DELETE)
@Component
public class QueryRoomMusicRequestHandler extends MessageHandler {

    private final Logger logger = Logger.getLogger(QueryRoomMusicRequestHandler.class.getName());

    @Autowired
    MusicListDaoService musicListDaoService;

    @Override
    protected Jinli.JinliMessageReply doHandle(ChannelHandlerContext ctx, Jinli.JinliMessageRequest messageRequest, User user) {
        Jinli.QueryRoomMusicRequest request = messageRequest.getQueryRoomMusicRequest();
        Jinli.QueryRoomMusicReply.Builder reply = Jinli.QueryRoomMusicReply.newBuilder();
        MusicList musicList = musicListDaoService.findByUserId(request.getUserId());
        if(Objects.isNull(musicList)) {
            musicList = MusicList.newInstance(request.getUserId());
            musicListDaoService.save(musicList);
        }
        if(request.getLiveUserDelayTime()!=null){
            reply.setLiveUserDelayTime(request.getLiveUserDelayTime());
        }
        reply.setPlayMusicInfo(musicList.playMusicToProto()).addAllMusicList(musicList.musicListToProto()).setVolume(musicList.getVolume());
        return buildReply(reply, SUCCESS);
    }
}
