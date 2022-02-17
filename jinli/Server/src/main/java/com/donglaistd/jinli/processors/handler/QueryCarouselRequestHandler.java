package com.donglaistd.jinli.processors.handler;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.builder.CarouselBuilder;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.system.carousel.BaseCarousel;
import io.netty.channel.ChannelHandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.donglaistd.jinli.util.MessageUtil.buildReply;

@Component
public class QueryCarouselRequestHandler extends MessageHandler{
    @Autowired
    CarouselBuilder carouselBuilder;

    @Override
    protected Jinli.JinliMessageReply doHandle(ChannelHandlerContext ctx, Jinli.JinliMessageRequest messageRequest, User user) {
        Jinli.QueryCarouselRequest request = messageRequest.getQueryCarouselRequest();
        Jinli.QueryCarouselReply.Builder reply = Jinli.QueryCarouselReply.newBuilder();
        Constant.CarouselType carouselType = request.getCarouselType();
        List<? extends BaseCarousel> carouselList= carouselBuilder.getCarouselListByType(carouselType);
        for (BaseCarousel carousel : carouselList) {
            reply.addCarousel(carousel.toProto());
        }
        return buildReply(reply, Constant.ResultCode.SUCCESS);
    }
}
