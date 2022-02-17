package com.donglaistd.jinli.http.entity;

import com.donglaistd.jinli.Constant;

public class CarouselRequest {
    private String id;
    private Constant.CarouselType carouselType;
    private String title;
    private String imageUrl;
    private Constant.RaceType raceType;
    private Integer raceFee;
    private String referenceId;

    public CarouselRequest() {
    }

    public CarouselRequest(String title, String imageUrl, Constant.RaceType raceType, Integer raceFee, String referenceId) {
        this.title = title;
        this.imageUrl = imageUrl;
        this.raceType = raceType;
        this.raceFee = raceFee;
        this.referenceId = referenceId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Constant.CarouselType getCarouselType() {
        return carouselType;
    }

    public void setCarouselType(Constant.CarouselType carouselType) {
        this.carouselType = carouselType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Constant.RaceType getRaceType() {
        return raceType;
    }

    public void setRaceType(Constant.RaceType raceType) {
        this.raceType = raceType;
    }

    public Integer getRaceFee() {
        return raceFee;
    }

    public void setRaceFee(Integer raceFee) {
        this.raceFee = raceFee;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }


    @Override
    public String toString() {
        return "CarouselRequest{" +
                "id='" + id + '\'' +
                ", carouselType=" + carouselType +
                ", title='" + title + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", raceType=" + raceType +
                ", raceFee=" + raceFee +
                ", referenceId='" + referenceId + '\'' +
                '}';
    }
}
