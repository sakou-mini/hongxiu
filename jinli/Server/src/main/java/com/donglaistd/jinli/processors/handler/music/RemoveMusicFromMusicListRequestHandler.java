package com.donglaistd.jinli.processors.handler.music;

import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.dao.MusicListDaoService;
import com.donglaistd.jinli.database.entity.Music;
import com.donglaistd.jinli.database.entity.MusicList;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.processors.handler.MessageHandler;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.donglaistd.jinli.Constant.ResultCode.*;
import static com.donglaistd.jinli.util.MessageUtil.buildReply;
//TODO (DELETE)
@Component
public class RemoveMusicFromMusicListRequestHandler extends MessageHandler {
    @Autowired
    MusicListDaoService musicListDaoService;
    @Override
    protected Jinli.JinliMessageReply doHandle(ChannelHandlerContext ctx, Jinli.JinliMessageRequest messageRequest, User user) {
        Jinli.RemoveMusicFromMusicListRequest request = messageRequest.getRemoveMusicFromMusicListRequest();
        Jinli.RemoveMusicFromMusicListReply.Builder reply = Jinli.RemoveMusicFromMusicListReply.newBuilder();
        Jinli.Music removeMusic = request.getMusic();
        if(removeMusic == null) return buildReply(reply, MISSING_OR_ILLEGAL_PARAMETERS);
        Music music = Music.newInstance(removeMusic.getTitle(), removeMusic.getSinger());
        MusicList musicList = musicListDaoService.findByUserId(user.getId());
        if(musicList==null)
            musicList = MusicList.newInstance(user.getId());
        Music playMusic = musicList.getPlayMusic();
        if(playMusic != null && playMusic.equals(music))
            return buildReply(reply, MUSIC_IS_PLAYING);
        if(!musicList.getMusicList().remove(music)) return buildReply(reply, MUSIC_NOT_EXIST);
        musicListDaoService.save(musicList);
        reply.setMusic(music.toProto());
        return buildReply(reply, SUCCESS);
    }
}
