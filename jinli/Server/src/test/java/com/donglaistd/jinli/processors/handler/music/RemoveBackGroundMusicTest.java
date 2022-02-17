package com.donglaistd.jinli.processors.handler.music;

import com.donglaistd.jinli.BaseTest;
import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.database.dao.MusicListDaoService;
import com.donglaistd.jinli.database.entity.Music;
import com.donglaistd.jinli.database.entity.MusicList;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.InvocationTargetException;

import static com.donglaistd.jinli.Constant.ResultCode.MUSIC_IS_PLAYING;

public class RemoveBackGroundMusicTest extends BaseTest {
    @Autowired
    RemoveMusicFromMusicListRequestHandler removeMusicFromMusicListRequestHandler;
    @Autowired
    MusicListDaoService musicListDaoService;
    @Autowired
    OperationRoomMusicRequestHandler operationRoomMusicRequestHandler;
    @Override
    public void setUp() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        super.setUp();
        addMusic(user.getId());
    }

    public void addMusic(String userId){
        MusicList musicList = MusicList.newInstance(userId);
        Music music1 = Music.newInstance("musicTitle1", "singer1", "5000", "asdasdasd/131211");
        Music music2 = Music.newInstance("musicTitle2", "singer2", "5000", "asdasdasd/131212");
        Music music3 = Music.newInstance("musicTitle3", "singer3", "5000", "asdasdasd/131213");
        musicList.addMusic(music1);
        musicList.addMusic(music2);
        musicList.addMusic(music3);
        Assert.assertEquals(3, musicList.getMusicList().size());
        musicListDaoService.save(musicList);
    }

    @Test
    public void removeBackgroundMusicSuccess(){
        MusicList musicList = musicListDaoService.findByUserId(user.getId());
        var music = musicList.getMusicList().get(0).toProto();
        var request = Jinli.JinliMessageRequest.newBuilder()
                .setRemoveMusicFromMusicListRequest(Jinli.RemoveMusicFromMusicListRequest.newBuilder().setMusic(music));
        var messageReply = removeMusicFromMusicListRequestHandler.handle(context, request.build());
        Assert.assertEquals(Constant.ResultCode.SUCCESS,messageReply.getResultCode());
    }

    @Test
    public void removeBackgroundMusicWhenItsPlaying(){
        var music = Jinli.Music.newBuilder().setTitle("musicTitle2").setSinger("singer2");
        var request = Jinli.JinliMessageRequest.newBuilder()
                .setOperationRoomMusicRequest(Jinli.OperationRoomMusicRequest.newBuilder().setMusic(music).setOperation(Constant.MusicStatue.MUSIC_PLAY));
        var messageReply = operationRoomMusicRequestHandler.handle(context, request.build());
        Assert.assertEquals(Constant.ResultCode.SUCCESS,messageReply.getResultCode());

        request.clear().setRemoveMusicFromMusicListRequest(Jinli.RemoveMusicFromMusicListRequest.newBuilder().setMusic(music));
        messageReply = removeMusicFromMusicListRequestHandler.handle(context, request.build());
        Assert.assertEquals(MUSIC_IS_PLAYING,messageReply.getResultCode());
    }
}
