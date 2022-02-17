package com.donglaistd.jinli.builder;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.dao.LiveUserDaoService;
import com.donglaistd.jinli.database.dao.system.carousel.LiveCarouselDaoService;
import com.donglaistd.jinli.database.entity.system.carousel.BaseCarousel;
import com.donglaistd.jinli.database.entity.system.carousel.LiveCarousel;
import com.donglaistd.jinli.http.entity.CarouselRequest;
import com.donglaistd.jinli.util.Pair;
import com.donglaistd.jinli.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

import static com.donglaistd.jinli.Constant.ResultCode.CAROUSEL_NOT_EXIT;
import static com.donglaistd.jinli.Constant.ResultCode.SUCCESS;

@Component
public class LiveCarouselBuilder {
    public final int MAX_LIVE_CAROUSEL = 10;

    @Autowired
    private LiveCarouselDaoService liveCarouselDaoService;

    @Autowired
    private LiveUserDaoService liveUserDaoService;

    public Pair<Constant.ResultCode, BaseCarousel> create(CarouselRequest request) {
        if(liveCarouselDaoService.countCarouselNum()>= MAX_LIVE_CAROUSEL)
            return new Pair<>(Constant.ResultCode.OVER_MAX_CAROUSEL_NUM,null);
        if(!StringUtils.isNullOrBlank(request.getReferenceId()) && Objects.isNull(liveUserDaoService.findById(request.getReferenceId()))){
            return new Pair<>(Constant.ResultCode.NOT_LIVE_USER,null);
        }
        LiveCarousel liveScroll = LiveCarousel.newInstance(request.getTitle(), request.getImageUrl(), System.currentTimeMillis(), request.getReferenceId());
        liveScroll = liveCarouselDaoService.save(liveScroll);
        return new Pair<>(Constant.ResultCode.SUCCESS, liveScroll);
    }

    public List<LiveCarousel> findAll(){
        return liveCarouselDaoService.findAll();
    }

    public Constant.ResultCode updateCarouse(CarouselRequest request) {
        LiveCarousel liveCarousel = liveCarouselDaoService.findById(request.getId());
        if (Objects.isNull(liveCarousel)) return CAROUSEL_NOT_EXIT;
        if(!StringUtils.isNullOrBlank(request.getReferenceId()) && Objects.isNull(liveUserDaoService.findById(request.getReferenceId()))){
            return Constant.ResultCode.NOT_LIVE_USER;
        }
        liveCarousel.setTitle(request.getTitle());
        liveCarousel.setImageUrl(request.getImageUrl());
        liveCarousel.setReferenceId(request.getReferenceId());
        liveCarouselDaoService.save(liveCarousel);
        return SUCCESS;
    }

    public void remove(String id) {
        liveCarouselDaoService.deleteById(id);
    }
}
