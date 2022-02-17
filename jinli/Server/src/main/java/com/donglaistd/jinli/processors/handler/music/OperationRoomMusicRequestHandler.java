package com.donglaistd.jinli.processors.handler.music;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.dao.MusicListDaoService;
import com.donglaistd.jinli.database.entity.*;
import com.donglaistd.jinli.processors.handler.MessageHandler;
import com.donglaistd.jinli.util.DataManager;
import com.donglaistd.jinli.util.StringUtils;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import static com.donglaistd.jinli.Constant.MusicStatue.*;
import static com.donglaistd.jinli.Constant.ResultCode.*;
import static com.donglaistd.jinli.util.MessageUtil.buildReply;
//TODO (DELETE)
@Component
public class OperationRoomMusicRequestHandler extends MessageHandler {
    private Logger logger = Logger.getLogger(OperationRoomMusicRequestHandler.class.getName());
    @Autowired
    MusicListDaoService musicListDaoService;

    @Override
    public Jinli.JinliMessageReply doHandle(ChannelHandlerContext ctx, Jinli.JinliMessageRequest messageRequest, User user) {
        Jinli.OperationRoomMusicRequest request = messageRequest.getOperationRoomMusicRequest();
        Jinli.OperationRoomMusicReply.Builder reply = Jinli.OperationRoomMusicReply.newBuilder();
        Constant.MusicStatue operation = request.getOperation();
        MusicList musicList = musicListDaoService.findByUserId(user.getId());
        LiveUser liveUser = dataManager.findLiveUser(user.getLiveUserId());

        if(liveUser == null ||  DataManager.findOnlineRoom(liveUser.getRoomId()) == null) {
            buildReply(reply, ROOM_DOES_NOT_EXIST);
        }
        if(musicList == null) musicList = MusicList.newInstance(user.getId());
        Constant.ResultCode resultCode = PARAM_ERROR;
        switch (operation){
            case MUSIC_PLAY:
                resultCode = playMusic(musicList,request.getMusic());
                break;
            case MUSIC_RESUME:
                resultCode = resumePlayMusic(musicList);
                break;
            case MUSIC_PAUSE:
                resultCode = pauseMusic(musicList);
                break;
            case MUSIC_STOP:
                resultCode = stopMusic(musicList);
        }
        if(Objects.equals(SUCCESS,resultCode)){
            musicListDaoService.save(musicList);
            broadcastBackgroundMusicChange(musicList,user,request.getLiveUserDelayTime());
        }
        return buildReply(reply, resultCode);
    }

    private Constant.ResultCode playMusic(MusicList musicList, Jinli.Music playMusic){
        if(playMusic == null) return PARAM_ERROR;
        Music music = Music.newInstance(playMusic.getTitle(), playMusic.getSinger());
        if(!musicList.containMusic(music))
            return MUSIC_NOT_EXIST;
        musicList.resetPlayMusic();
        musicList.setPlayStartTime(System.currentTimeMillis());
        musicList.setStatue(MUSIC_PLAY);
        musicList.setPlayMusic(musicList.getMusicList().stream().filter(music::equals).findFirst().orElse(null));
        String playTime = new SimpleDateFormat("yyyy-MM-dd : hh:mm:ss").format(new Date(musicList.getPlayStartTime()));
        logger.info("music is start when:--->"+playTime);
        return SUCCESS;
    }

    private Constant.ResultCode resumePlayMusic(MusicList musicList){
        if(!Objects.equals(MUSIC_PAUSE,musicList.getStatue())){
            return MUSIC_NOT_PAUSE;
        }
        musicList.setPlayStartTime(System.currentTimeMillis());
        musicList.setPauseTime(0);
        musicList.setStatue(MUSIC_PLAY);
        String pauseTimeStr = new SimpleDateFormat("yyyy-MM-dd : hh:mm:ss").format(new Date(musicList.getPlayStartTime()));
        logger.info("music is resume when:--->"+pauseTimeStr  +"  --->playTime is--->"+ TimeUnit.MILLISECONDS.toSeconds(musicList.getPlayTime()));
        return SUCCESS;
    }

    private Constant.ResultCode pauseMusic(MusicList musicList){
        if(!Objects.equals(MUSIC_PLAY,musicList.getStatue())){
            return MUSIC_NOT_PLAYING;
        }
        long pauseTime = System.currentTimeMillis();
        long playTime = pauseTime - musicList.getPlayStartTime();
        musicList.setPauseTime(pauseTime);
        musicList.addPlayTime(playTime);
        musicList.setStatue(MUSIC_PAUSE);
        String pauseTimeStr = new SimpleDateFormat("yyyy-MM-dd : hh:mm:ss").format(new Date(pauseTime));
        logger.info("music is pause when:--->"+pauseTimeStr  +"  ----->playTime is--->"+ TimeUnit.MILLISECONDS.toSeconds(musicList.getPlayTime()));
        return SUCCESS;
    }

    private Constant.ResultCode stopMusic(MusicList musicList){
        musicList.resetPlayMusic();
        musicList.setStatue(MUSIC_STOP);
        return SUCCESS;
    }

    private void broadcastBackgroundMusicChange(MusicList musicList,User user,String delayTime){
        Jinli.BackgroundMusicChangeBroadcastMessage.Builder backgroundMusicChange = Jinli.BackgroundMusicChangeBroadcastMessage.newBuilder();
        backgroundMusicChange.setPlayMusicInfo(musicList.playMusicToProto()).setVolume(musicList.getVolume());
        Room room = DataManager.findRoomByLiveUserId(user.getLiveUserId());
        if(room != null){
            if(!StringUtils.isNullOrBlank(delayTime))
                backgroundMusicChange.setLiveUserDelayTime(delayTime);
            room.broadCastToAllPlatform(Jinli.JinliMessageReply.newBuilder().setBackgroundMusicChangeBroadcastMessage(backgroundMusicChange).build());
        }
    }
}
