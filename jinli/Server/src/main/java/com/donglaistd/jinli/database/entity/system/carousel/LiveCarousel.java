package com.donglaistd.jinli.database.entity.system.carousel;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
@Document
public class LiveCarousel extends BaseCarousel {
    @Id
    protected ObjectId id = ObjectId.get();
    @Field
    private String referenceId;

    public LiveCarousel() {
    }

    public LiveCarousel(String title, String imageUrl, long createTime, String referenceId) {
        super(title, imageUrl, createTime);
        this.referenceId = referenceId;
    }

    public static LiveCarousel newInstance(String title, String imageUrl, long createTime, String referenceId) {
        return new LiveCarousel(title, imageUrl, createTime, referenceId);
    }

    @Override
    public Constant.CarouselType getCarouselType() {
        return Constant.CarouselType.LIVE_CAROUSEL;
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

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public long getCreateTime() {
        return this.createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    @Override
    public Jinli.Carousel toProto() {
        return Jinli.Carousel.newBuilder().setTitle(title).setImageUrl(imageUrl)
                .setCarouselType(getCarouselType()).setReferenceId(referenceId).build();
    }
}
