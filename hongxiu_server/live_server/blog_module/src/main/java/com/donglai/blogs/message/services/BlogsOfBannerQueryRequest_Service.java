package com.donglai.blogs.message.services;

import com.donglai.common.constant.BannerStatueEnum;
import com.donglai.common.dispatcher.TopicMessageServiceI;
import com.donglai.model.db.entity.live.Banner;
import com.donglai.model.db.service.live.BannerService;
import com.donglai.protocol.Blog;
import com.donglai.protocol.Constant;
import com.donglai.protocol.HongXiu;
import com.donglai.protocol.message.KafkaMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.donglai.blogs.util.MessageUtil.buildReply;


/**
 * @author Moon
 * @date 2021-12-20 16:06
 */
@Service("BlogsOfBannerQueryRequest")
public class BlogsOfBannerQueryRequest_Service implements TopicMessageServiceI<String> {
    @Autowired
    private BannerService bannerService;

    @Override
    public KafkaMessage.TopicMessage Process(String userId, HongXiu.HongXiuMessageRequest message, Object... param) {
        Blog.BlogsOfBannerQueryReply.Builder builder = Blog.BlogsOfBannerQueryReply.newBuilder();
        List<Banner> banners = bannerService.findAllByStatusIsFalseOrderBySortDesc();
        for (Banner banner : banners) {
            if(banner.isOver()){
                banner.setStatus(BannerStatueEnum.OVER.getValue());
                bannerService.save(banner);
            }
            builder.addBanner(banner.toProto());
        }
        return buildReply(userId, builder, Constant.ResultCode.SUCCESS);
    }

    @Override
    public void Close(String s) {

    }
}
