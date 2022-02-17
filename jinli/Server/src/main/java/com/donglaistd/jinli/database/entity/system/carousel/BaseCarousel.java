package com.donglaistd.jinli.database.entity.system.carousel;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import org.springframework.data.mongodb.core.mapping.Field;

public abstract class BaseCarousel {
    @Field
    public String title;
    @Field
    public String imageUrl;
    @Field
    public long createTime;

    public BaseCarousel() {
    }

    public BaseCarousel(String title, String imageUrl, long createTime) {
        this.title = title;
        this.imageUrl = imageUrl;
        this.createTime = createTime;
    }

    public abstract Constant.CarouselType getCarouselType();

    public abstract String getId();

    public abstract String getTitle();

    public abstract void setTitle(String title);

    public abstract String getImageUrl();

    public abstract void setImageUrl(String imageUrl);

    public abstract long getCreateTime();

    public abstract void setCreateTime(long createTime);

    public abstract Jinli.Carousel toProto();
}
