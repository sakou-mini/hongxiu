package com.donglaistd.jinli.processors.handler;

import com.donglaistd.jinli.BaseTest;
import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.builder.CarouselBuilder;
import com.donglaistd.jinli.http.entity.CarouselRequest;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class QueryCarouselRequestHandlerTest extends BaseTest {
    @Autowired
    QueryCarouselRequestHandler queryCarouselRequestHandler;
    @Autowired
    CarouselBuilder carouselBuilder;

    @Test
    public void test(){
        CarouselRequest carouselRequest = new CarouselRequest("title","image", Constant.RaceType.LANDLORDS,1000,"refId");
        carouselRequest.setCarouselType(Constant.CarouselType.RACE_CAROUSEL);
        carouselBuilder.create(carouselRequest);

        carouselRequest.setTitle("title2");
        carouselBuilder.create(carouselRequest);

        carouselRequest.setCarouselType(Constant.CarouselType.LIVE_CAROUSEL);
        carouselBuilder.create(carouselRequest);

        Jinli.QueryCarouselRequest.Builder builder = Jinli.QueryCarouselRequest.newBuilder().setCarouselType(Constant.CarouselType.RACE_CAROUSEL);
        Jinli.JinliMessageRequest request = Jinli.JinliMessageRequest.newBuilder().setQueryCarouselRequest(builder.build()).build();
        Jinli.JinliMessageReply messageReply = queryCarouselRequestHandler.doHandle(context, request, user);
        Jinli.QueryCarouselReply reply = messageReply.getQueryCarouselReply();
        List<Jinli.Carousel> carouselList = reply.getCarouselList();
        Assert.assertEquals(2,carouselList.size());
        Assert.assertEquals(carouselList.get(0).getCarouselType(),Constant.CarouselType.RACE_CAROUSEL);

        builder.setCarouselType(Constant.CarouselType.LIVE_CAROUSEL);
        request = Jinli.JinliMessageRequest.newBuilder().setQueryCarouselRequest(builder.build()).build();
        messageReply = queryCarouselRequestHandler.doHandle(context, request, user);
        reply = messageReply.getQueryCarouselReply();
        carouselList = reply.getCarouselList();
        Assert.assertEquals(1,carouselList.size());
        Assert.assertEquals(carouselList.get(0).getCarouselType(),Constant.CarouselType.LIVE_CAROUSEL);
    }
}
