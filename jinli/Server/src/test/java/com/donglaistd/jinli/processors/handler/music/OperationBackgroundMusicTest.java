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

import static com.donglaistd.jinli.Constant.MusicStatue.*;

public class OperationBackgroundMusicTest extends BaseTest {
    @Autowired
    MusicListDaoService musicListDaoService;
    @Autowired
    QueryRoomMusicRequestHandler queryRoomMusicRequestHandler;
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
    public void queryRoomMusicListTest(){
        Jinli.QueryRoomMusicRequest.Builder builder = Jinli.QueryRoomMusicRequest.newBuilder().setUserId(user.getId());
        var requestWrapper = Jinli.JinliMessageRequest.newBuilder().setQueryRoomMusicRequest(builder);
        Jinli.JinliMessageReply result = queryRoomMusicRequestHandler.handle(context, requestWrapper.build());
        Jinli.QueryRoomMusicReply reply = result.getQueryRoomMusicReply();
        Assert.assertEquals(3,reply.getMusicListCount());
        Jinli.PlayedMusicInfo playMusicInfo = reply.getPlayMusicInfo();
        Assert.assertEquals(MUSIC_STOP,playMusicInfo.getStatue());
    }

    @Test
    public void playMusicTest() throws InterruptedException {
        Jinli.Music playMusic = Jinli.Music.newBuilder().setTitle("musicTitle1").setSinger("singer1").build();
        Jinli.OperationRoomMusicRequest.Builder playMusicRequest = Jinli.OperationRoomMusicRequest.newBuilder().setMusic(playMusic).setOperation(MUSIC_PLAY);
        var requestWrapper = Jinli.JinliMessageRequest.newBuilder().setOperationRoomMusicRequest(playMusicRequest);
        Jinli.JinliMessageReply result = operationRoomMusicRequestHandler.handle(context, requestWrapper.build());
        Jinli.OperationRoomMusicReply reply = result.getOperationRoomMusicReply();
        Assert.assertEquals(Constant.ResultCode.SUCCESS,result.getResultCode());

        Jinli.QueryRoomMusicRequest.Builder queryRoomMusic = Jinli.QueryRoomMusicRequest.newBuilder().setUserId(user.getId());
        requestWrapper.clear().setQueryRoomMusicRequest(queryRoomMusic);
        result = queryRoomMusicRequestHandler.handle(context, requestWrapper.build());
        Jinli.QueryRoomMusicReply queryRoomReply = result.getQueryRoomMusicReply();
        Assert.assertEquals(3,queryRoomReply.getMusicListCount());
        Jinli.PlayedMusicInfo playMusicInfo = queryRoomReply.getPlayMusicInfo();
        Assert.assertEquals(MUSIC_PLAY,playMusicInfo.getStatue());
    }

    @Test
    public void pauseMusicAndResumeMusicTest() throws InterruptedException {
        //1.play music
        Jinli.Music playMusic = Jinli.Music.newBuilder().setTitle("musicTitle1").setSinger("singer1").build();
        Jinli.OperationRoomMusicRequest.Builder playMusicRequest = Jinli.OperationRoomMusicRequest.newBuilder().setMusic(playMusic).setOperation(MUSIC_PLAY);
        var requestWrapper = Jinli.JinliMessageRequest.newBuilder().setOperationRoomMusicRequest(playMusicRequest);
        Jinli.JinliMessageReply result = operationRoomMusicRequestHandler.handle(context, requestWrapper.build());
        Jinli.OperationRoomMusicReply reply = result.getOperationRoomMusicReply();
        Assert.assertEquals(Constant.ResultCode.SUCCESS,result.getResultCode());

        Thread.sleep(2000);
        //2. pauseMusic
        playMusicRequest = Jinli.OperationRoomMusicRequest.newBuilder().setOperation(MUSIC_PAUSE);
        requestWrapper = Jinli.JinliMessageRequest.newBuilder().setOperationRoomMusicRequest(playMusicRequest);
        result = operationRoomMusicRequestHandler.handle(context, requestWrapper.build());
        Assert.assertEquals(Constant.ResultCode.SUCCESS,result.getResultCode());

        //3.query Music
        Jinli.QueryRoomMusicRequest.Builder queryRoomMusic = Jinli.QueryRoomMusicRequest.newBuilder().setUserId(user.getId());
        requestWrapper.clear().setQueryRoomMusicRequest(queryRoomMusic);
        result = queryRoomMusicRequestHandler.handle(context, requestWrapper.build());
        Jinli.QueryRoomMusicReply queryRoomReply = result.getQueryRoomMusicReply();
        Assert.assertEquals(3,queryRoomReply.getMusicListCount());
        Jinli.PlayedMusicInfo playMusicInfo = queryRoomReply.getPlayMusicInfo();
        Assert.assertEquals(MUSIC_PAUSE,playMusicInfo.getStatue());
        var playTime = Long.parseLong(playMusicInfo.getPlayTime());
        Assert.assertTrue(playTime >= 2000 && playTime<3000);

        Thread.sleep(2000);
        //4.resumeMusic
        playMusicRequest = Jinli.OperationRoomMusicRequest.newBuilder().setOperation(MUSIC_RESUME);
        requestWrapper = Jinli.JinliMessageRequest.newBuilder().setOperationRoomMusicRequest(playMusicRequest);
        result = operationRoomMusicRequestHandler.handle(context, requestWrapper.build());
        Assert.assertEquals(Constant.ResultCode.SUCCESS,result.getResultCode());

        Thread.sleep(2000);
        //5.queryMusic
        queryRoomMusic = Jinli.QueryRoomMusicRequest.newBuilder().setUserId(user.getId());
        requestWrapper.clear().setQueryRoomMusicRequest(queryRoomMusic);
        result = queryRoomMusicRequestHandler.handle(context, requestWrapper.build());
        playMusicInfo = result.getQueryRoomMusicReply().getPlayMusicInfo();
        Assert.assertEquals(MUSIC_PLAY,playMusicInfo.getStatue());
        playTime = Long.parseLong(playMusicInfo.getPlayTime());
        Assert.assertTrue(playTime >= 4000 && playTime <= 5000);
    }

    @Test
    public void stopMusicTest() throws InterruptedException {
        //1.play music
        Jinli.Music playMusic = Jinli.Music.newBuilder().setTitle("musicTitle1").setSinger("singer1").build();
        Jinli.OperationRoomMusicRequest.Builder playMusicRequest = Jinli.OperationRoomMusicRequest.newBuilder().setMusic(playMusic).setOperation(MUSIC_PLAY);
        var requestWrapper = Jinli.JinliMessageRequest.newBuilder().setOperationRoomMusicRequest(playMusicRequest);
        Jinli.JinliMessageReply messReply = operationRoomMusicRequestHandler.handle(context, requestWrapper.build());
        Jinli.OperationRoomMusicReply reply = messReply.getOperationRoomMusicReply();
        Assert.assertEquals(Constant.ResultCode.SUCCESS,messReply.getResultCode());

        Thread.sleep(1000);
        requestWrapper.clear().setOperationRoomMusicRequest(Jinli.OperationRoomMusicRequest.newBuilder().setOperation(MUSIC_STOP));
        messReply = operationRoomMusicRequestHandler.handle(context, requestWrapper.build());
        Assert.assertEquals(Constant.ResultCode.SUCCESS,messReply.getResultCode());

        Jinli.QueryRoomMusicRequest.Builder queryRoomMusic = Jinli.QueryRoomMusicRequest.newBuilder().setUserId(user.getId());
        requestWrapper.clear().setQueryRoomMusicRequest(queryRoomMusic);
        messReply = queryRoomMusicRequestHandler.handle(context, requestWrapper.build());
        Jinli.QueryRoomMusicReply queryRoomReply = messReply.getQueryRoomMusicReply();
        Assert.assertEquals(3,queryRoomReply.getMusicListCount());
        Jinli.PlayedMusicInfo playMusicInfo = queryRoomReply.getPlayMusicInfo();
        Assert.assertEquals(MUSIC_STOP,playMusicInfo.getStatue());
    }

}
