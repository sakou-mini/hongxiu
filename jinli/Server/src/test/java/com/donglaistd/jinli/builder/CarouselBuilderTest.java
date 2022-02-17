package com.donglaistd.jinli.builder;

import com.donglaistd.jinli.BaseTest;
import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.entity.system.carousel.BaseCarousel;
import com.donglaistd.jinli.http.entity.CarouselRequest;
import com.donglaistd.jinli.util.Pair;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class CarouselBuilderTest extends BaseTest {
    @Autowired
    CarouselBuilder carouselBuilder;

    @Test
    public void createCarouselTest(){
        CarouselRequest request = new CarouselRequest("title","image", Constant.RaceType.LANDLORDS,1000,"");
        request.setCarouselType(Constant.CarouselType.GUESS_CAROUSEL);
        Pair<Constant.ResultCode, BaseCarousel> result = carouselBuilder.create(request);
        Assert.assertEquals(Constant.ResultCode.SUCCESS,result.getLeft());
        Assert.assertEquals(Constant.CarouselType.GUESS_CAROUSEL,result.getRight().getCarouselType());

        request.setCarouselType(Constant.CarouselType.LIVE_CAROUSEL);
        result = carouselBuilder.create(request);
        Assert.assertEquals(Constant.ResultCode.SUCCESS,result.getLeft());
        Assert.assertEquals(Constant.CarouselType.LIVE_CAROUSEL,result.getRight().getCarouselType());

        request.setCarouselType(Constant.CarouselType.RACE_CAROUSEL);
        result = carouselBuilder.create(request);
        Assert.assertEquals(Constant.ResultCode.SUCCESS,result.getLeft());
        Assert.assertEquals(Constant.CarouselType.RACE_CAROUSEL,result.getRight().getCarouselType());
    }

}
