package com.donglaistd.jinli.database.dao.system.carousel;

import com.donglaistd.jinli.database.entity.system.carousel.RaceCarousel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RaceCarouselDaoService {
    @Autowired
    RaceCarouselRepository raceCarouselRepository;

    public RaceCarousel save(RaceCarousel raceScroll) {
        return raceCarouselRepository.save(raceScroll);
    }

    public void deleteById(String id) {
        raceCarouselRepository.deleteById(id);
    }

    public long countCarouselNum(){
        return raceCarouselRepository.count();
    }

    public List<RaceCarousel> findAll(){
        return raceCarouselRepository.findAll(Sort.by(Sort.Direction.ASC, "createTime"));
    }

    public RaceCarousel findById(String id) {
        return raceCarouselRepository.findById(id).orElse(null);
    }
}
