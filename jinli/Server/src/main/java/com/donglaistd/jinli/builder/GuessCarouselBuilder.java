package com.donglaistd.jinli.builder;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.dao.GuessDaoService;
import com.donglaistd.jinli.database.dao.system.carousel.GuessCarouselDaoService;
import com.donglaistd.jinli.database.entity.system.carousel.BaseCarousel;
import com.donglaistd.jinli.database.entity.system.carousel.GuessCarousel;
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
public class GuessCarouselBuilder {
    public final int MAX_GUESS_CAROUSEL = 10;

    @Autowired
    GuessCarouselDaoService guessCarouselDaoService;
    @Autowired
    GuessDaoService guessDaoService;

    public Pair<Constant.ResultCode, BaseCarousel> create(CarouselRequest request) {
        if(guessCarouselDaoService.countCarouselNum()>= MAX_GUESS_CAROUSEL) {
            return new Pair<>(Constant.ResultCode.OVER_MAX_CAROUSEL_NUM, null);
        }
        if(!StringUtils.isNullOrBlank(request.getReferenceId()) && Objects.isNull(guessDaoService.findById(request.getReferenceId()))){
            return new Pair<>(Constant.ResultCode.GUESS_WAGER_NOT_EXIT, null);
        }
        GuessCarousel guessCarousel = GuessCarousel.newInstance(request.getTitle(), request.getImageUrl(), System.currentTimeMillis(), request.getReferenceId());
        guessCarousel = guessCarouselDaoService.save(guessCarousel);
        return new Pair<>(Constant.ResultCode.SUCCESS, guessCarousel);
    }

    public List<GuessCarousel> findAll(){
        return guessCarouselDaoService.findAll();
    }

    public Constant.ResultCode updateCarouse(CarouselRequest request) {
        GuessCarousel guessCarousel = guessCarouselDaoService.findById(request.getId());
        if (Objects.isNull(guessCarousel)) {
            return CAROUSEL_NOT_EXIT;
        }
        if(!StringUtils.isNullOrBlank(request.getReferenceId()) && Objects.isNull(guessDaoService.findById(request.getReferenceId()))){
            return Constant.ResultCode.GUESS_WAGER_NOT_EXIT;
        }
        guessCarousel.setTitle(request.getTitle());
        guessCarousel.setImageUrl(request.getImageUrl());
        guessCarousel.setReferenceId(request.getReferenceId());
        guessCarouselDaoService.save(guessCarousel);
        return SUCCESS;
    }

    public void remove(String id) {
        guessCarouselDaoService.deleteById(id);
    }
}
