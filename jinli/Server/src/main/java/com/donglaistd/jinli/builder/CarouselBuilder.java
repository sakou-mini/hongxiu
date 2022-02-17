package com.donglaistd.jinli.builder;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.dao.system.carousel.GuessCarouselDaoService;
import com.donglaistd.jinli.database.dao.system.carousel.LiveCarouselDaoService;
import com.donglaistd.jinli.database.dao.system.carousel.RaceCarouselDaoService;
import com.donglaistd.jinli.database.entity.system.carousel.BaseCarousel;
import com.donglaistd.jinli.http.entity.CarouselRequest;
import com.donglaistd.jinli.util.Pair;
import com.donglaistd.jinli.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

@Component
public class CarouselBuilder {
    private static final Logger logger = Logger.getLogger(CarouselBuilder.class.getName());
    @Autowired
    RaceCarouselBuilder raceCarouselBuilder;
    @Autowired
    LiveCarouselBuilder liveCarouselBuilder;
    @Autowired
    GuessCarouselBuilder guessCarouselBuilder;
    @Autowired
    RaceCarouselDaoService raceCarouselDaoService;
    @Autowired
    LiveCarouselDaoService liveCarouselDaoService;
    @Autowired
    GuessCarouselDaoService guessCarouselDaoService;

    public Pair<Constant.ResultCode,BaseCarousel> create(CarouselRequest request){
        Constant.CarouselType scrollType = request.getCarouselType();
        logger.info("create scroll by:"+scrollType +"requestInfo is:"+request);
        long createTime = System.currentTimeMillis();
        if(Objects.isNull(scrollType) || StringUtils.isNullOrBlank(request.getTitle()) || StringUtils.isNullOrBlank(request.getImageUrl()))
            return new Pair<>(Constant.ResultCode.PARAM_ERROR, null);
        switch (scrollType) {
            case RACE_CAROUSEL:
                return raceCarouselBuilder.create(request);
            case LIVE_CAROUSEL:
                return liveCarouselBuilder.create(request);
            case GUESS_CAROUSEL:
                return guessCarouselBuilder.create(request);
        }
        return  new Pair<>(Constant.ResultCode.NO_CAROUSEL_TYPE, null);
    }

    public List<? extends BaseCarousel> getCarouselListByType(Constant.CarouselType carouselType){
        switch (carouselType){
            case RACE_CAROUSEL:
                return raceCarouselBuilder.findAll();
            case LIVE_CAROUSEL:
                return liveCarouselBuilder.findAll();
            case GUESS_CAROUSEL:
                return guessCarouselBuilder.findAll();
        }
        return new ArrayList<>(0);
    }

    public Constant.ResultCode updateCarouselData(CarouselRequest request){
        Constant.CarouselType carouselType = request.getCarouselType();
        if(StringUtils.isNullOrBlank(request.getId()) || Objects.isNull(carouselType))
            return Constant.ResultCode.PARAM_ERROR;
        switch (carouselType) {
            case RACE_CAROUSEL:
                return raceCarouselBuilder.updateCarouse(request);
            case LIVE_CAROUSEL:
                return liveCarouselBuilder.updateCarouse(request);
            case GUESS_CAROUSEL:
                return guessCarouselBuilder.updateCarouse(request);
        }
        return Constant.ResultCode.CARDS_NOT_EXIST;
    }

    public Constant.ResultCode removeCarousel(String id, Constant.CarouselType carouselType) {
        if(StringUtils.isNullOrBlank(id) || Objects.isNull(carouselType))
            return Constant.ResultCode.PARAM_ERROR;
        switch (carouselType){
            case RACE_CAROUSEL:
                raceCarouselBuilder.remove(id);
                break;
            case LIVE_CAROUSEL:
                liveCarouselBuilder.remove(id);
                break;
            case GUESS_CAROUSEL:
                guessCarouselBuilder.remove(id);
                break;
        }
        return Constant.ResultCode.SUCCESS;
    }

    public BaseCarousel findById(String id, Constant.CarouselType carouselType){
        switch (carouselType){
            case RACE_CAROUSEL:
                return raceCarouselDaoService.findById(id);
            case LIVE_CAROUSEL:
                return liveCarouselDaoService.findById(id);
            case GUESS_CAROUSEL:
                return guessCarouselDaoService.findById(id);
        }
        return null;
    }
}
