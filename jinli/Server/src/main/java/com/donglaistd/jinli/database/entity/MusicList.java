package com.donglaistd.jinli.database.entity;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.persistence.Id;
import java.util.ArrayList;
import java.util.List;

@Document
public class MusicList {
    @Id
    private ObjectId id;
    @Field
    @Indexed(unique = true)
    private String userId;
    @Field
    private Constant.MusicStatue statue = Constant.MusicStatue.MUSIC_STOP;
    @Field
    private long playStartTime;
    @Field
    private long playTime;
    @Field
    private long pauseTime;
    @Field
    private Music playMusic;
    @Field
    private int volume = 100;
    @Field
    private List<Music> musicList = new ArrayList<>();

    public MusicList() {
    }

    private MusicList(String userId) {
        this.userId = userId;
    }

    public static MusicList newInstance(String userId){
        return new MusicList(userId);
    }

    public String getId() {
        return id.toString();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getPlayStartTime() {
        return playStartTime;
    }

    public void setPlayStartTime(long playStartTime) {
        this.playStartTime = playStartTime;
    }

    public long getPauseTime() {
        return pauseTime;
    }

    public void setPauseTime(long pauseTime) {
        this.pauseTime = pauseTime;
    }

    public List<Music> getMusicList() {
        return musicList;
    }

    public void setMusicList(List<Music> musicList) {
        this.musicList = musicList;
    }

    public Constant.MusicStatue getStatue() {
        return statue;
    }

    public void setStatue(Constant.MusicStatue statue) {
        this.statue = statue;
    }

    public boolean containMusic(Music music){
        return musicList.contains(music);
    }

    public Music getPlayMusic() {
        return playMusic;
    }

    public void setPlayMusic(Music playMusic) {
        this.playMusic = playMusic;
    }

    public boolean addMusic(Music music){
        if(!musicList.contains(music))
            return musicList.add(music);
        return false;
    }

    public long getPlayTime() {
        return playTime;
    }

    public void setPlayTime(long playTime) {
        this.playTime = playTime;
    }
    public void addPlayTime(long playTime){
        this.playTime += playTime;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public long getPlayTimeForNow(){
        if(statue.equals(Constant.MusicStatue.MUSIC_PLAY)){
            return playTime +( System.currentTimeMillis() - playStartTime);
        }
        return playTime;
    }

    public Jinli.PlayedMusicInfo playMusicToProto(){
        Jinli.PlayedMusicInfo.Builder builder = Jinli.PlayedMusicInfo.newBuilder().setStatue(statue);
        long currentPlayTime = getPlayTimeForNow();
        if(currentPlayTime>0)
            builder.setPlayTime(String.valueOf(currentPlayTime));
        if(playMusic != null){
            builder.setMusic(playMusic.toProto());
        }
        return builder.build();
    }

    public List<Jinli.Music> musicListToProto(){
        List<Jinli.Music> protoMusicList = new ArrayList<>(musicList.size());
        for (Music music : musicList) {
            protoMusicList.add(music.toProto());
        }
        return protoMusicList;
    }

    public void resetPlayMusic(){
        this.playStartTime = 0;
        this.playTime = 0;
        this.pauseTime = 0;
        this.playMusic = null;
    }
}
