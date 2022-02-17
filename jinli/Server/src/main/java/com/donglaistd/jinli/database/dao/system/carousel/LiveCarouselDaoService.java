package com.donglaistd.jinli.database.dao.system.carousel;

import com.donglaistd.jinli.database.entity.system.carousel.LiveCarousel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LiveCarouselDaoService {
    @Autowired
    LiveCarouselRepository liveCarouselRepository;

    public LiveCarousel save(LiveCarousel liveScroll) {
        return liveCarouselRepository.save(liveScroll);
    }

    public void deleteById(String id) {
        liveCarouselRepository.deleteById(id);
    }

    public long countCarouselNum(){
        return liveCarouselRepository.count();
    }

    public List<LiveCarousel> findAll(){
        return liveCarouselRepository.findAll(Sort.by(Sort.Direction.ASC, "createTime"));
    }

    public LiveCarousel findById(String id){
        return liveCarouselRepository.findById(id).orElse(null);
    }
}
