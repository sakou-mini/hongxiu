package com.donglaistd.jinli.database.entity.system.carousel;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
@Document
public class RaceCarousel extends BaseCarousel {
    @Id
    protected ObjectId id = ObjectId.get();
    @Field
    private Constant.RaceType raceType;
    @Field
    private int raceFee;

    public RaceCarousel() {
    }

    public RaceCarousel(String title, String imageUrl, long createTime, Constant.RaceType raceType, int raceFee) {
        super(title, imageUrl, createTime);
        this.raceType = raceType;
        this.raceFee = raceFee;
    }

    public static RaceCarousel newInstance(String title, String imageUrl, long createTime, Constant.RaceType raceType, int raceFee){
        return new RaceCarousel(title, imageUrl, createTime, raceType, raceFee);
    }

    @Override
    public Constant.CarouselType getCarouselType() {
        return Constant.CarouselType.RACE_CAROUSEL;
    }

    @Override
    public String getId() {
        return id.toString();
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String getImageUrl() {
        return imageUrl;
    }

    @Override
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public long getCreateTime() {
        return this.createTime;
    }

    @Override
    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public Constant.RaceType getRaceType() {
        return raceType;
    }

    public void setRaceType(Constant.RaceType raceType) {
        this.raceType = raceType;
    }

    public int getRaceFee() {
        return raceFee;
    }

    public void setRaceFee(int raceFee) {
        this.raceFee = raceFee;
    }

    @Override
    public Jinli.Carousel toProto() {
        Jinli.RaceCarouselConfig.Builder raceCarouselConfig = Jinli.RaceCarouselConfig.newBuilder().setRaceFee(raceFee).setRaceType(raceType);
        return Jinli.Carousel.newBuilder().setTitle(title).setImageUrl(imageUrl)
                .setCarouselType(getCarouselType()).setRaceCarouselConfig(raceCarouselConfig).build();
    }
}
