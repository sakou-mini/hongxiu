package com.donglaistd.jinli.database.entity;

import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.util.StringUtils;

import java.io.Serializable;
import java.util.Objects;

public class Music implements Serializable {
    private String title;
    private String singer;
    private String musicTime;
    private String resourceUrl;

    public Music() {
    }

    private Music(String title, String singer, String musicTime, String resourceUrl) {
        this.title = title;
        this.singer = singer;
        this.musicTime = musicTime;
        this.resourceUrl = resourceUrl;
    }

    public Music(String title, String singer) {
        this.title = title;
        this.singer = singer;
    }

    public static Music newInstance(String title, String singer, String musicTime, String resourceUrl){
        return new Music(title, singer, musicTime, resourceUrl);
    }

    public static Music newInstance(String title, String singer){
        return new Music(title, singer);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public String getMusicTime() {
        return musicTime;
    }

    public void setMusicTime(String musicTime) {
        this.musicTime = musicTime;
    }

    public String getResourceUrl() {
        return resourceUrl;
    }

    public void setResourceUrl(String resourceUrl) {
        this.resourceUrl = resourceUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Music music = (Music) o;
        return title.equals(music.title) &&
                singer.equals(music.singer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, singer);
    }

    public Jinli.Music toProto(){
        Jinli.Music.Builder musicBuilder = Jinli.Music.newBuilder().setSinger(singer).setTitle(title);
        if(!StringUtils.isNullOrBlank(musicTime)){
            musicBuilder.setMusicTime(musicTime);
        }
        if(!StringUtils.isNullOrBlank(resourceUrl)){
            musicBuilder.setResourceUrl(resourceUrl);
        }
        return musicBuilder.build();
    }
}
