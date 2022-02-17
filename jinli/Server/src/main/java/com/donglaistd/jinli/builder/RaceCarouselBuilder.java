package com.donglaistd.jinli.builder;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.dao.system.carousel.RaceCarouselDaoService;
import com.donglaistd.jinli.database.entity.system.carousel.BaseCarousel;
import com.donglaistd.jinli.database.entity.system.carousel.RaceCarousel;
import com.donglaistd.jinli.http.entity.CarouselRequest;
import com.donglaistd.jinli.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

import static com.donglaistd.jinli.Constant.ResultCode.CAROUSEL_NOT_EXIT;
import static com.donglaistd.jinli.Constant.ResultCode.SUCCESS;

@Component
public class RaceCarouselBuilder {
    public final int MAX_RACE_CAROUSEL = 10;
    @Autowired
    RaceCarouselDaoService raceCarouselDaoService;

    public  Pair<Constant.ResultCode, BaseCarousel> create(CarouselRequest request) {
        if(raceCarouselDaoService.countCarouselNum()>= MAX_RACE_CAROUSEL) {
            return new Pair<>(Constant.ResultCode.OVER_MAX_CAROUSEL_NUM, null);
        }
        if(Objects.isNull(request.getRaceFee()) || Objects.isNull(request.getRaceType())) {
            return new Pair<>(Constant.ResultCode.PARAM_ERROR, null);
        }
        RaceCarousel raceScroll = RaceCarousel.newInstance(request.getTitle(), request.getImageUrl(), System.currentTimeMillis(), request.getRaceType(),request.getRaceFee());
        raceScroll = raceCarouselDaoService.save(raceScroll);
        return new Pair<>(SUCCESS, raceScroll);
    }

    public List<RaceCarousel> findAll(){
        return raceCarouselDaoService.findAll();
    }

    public Constant.ResultCode updateCarouse(CarouselRequest request) {
        RaceCarousel raceCarousel = raceCarouselDaoService.findById(request.getId());
        if (Objects.isNull(raceCarousel)) {
            return CAROUSEL_NOT_EXIT;
        }
        raceCarousel.setTitle(request.getTitle());
        raceCarousel.setImageUrl(request.getImageUrl());
        raceCarousel.setRaceFee(request.getRaceFee());
        raceCarousel.setRaceType(request.getRaceType());
        raceCarouselDaoService.save(raceCarousel);
        return SUCCESS;
    }

    public void remove(String id) {
        raceCarouselDaoService.deleteById(id);
    }
}
