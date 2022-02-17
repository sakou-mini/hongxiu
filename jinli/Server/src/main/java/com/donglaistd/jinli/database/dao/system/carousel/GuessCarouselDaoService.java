package com.donglaistd.jinli.database.dao.system.carousel;

import com.donglaistd.jinli.database.entity.system.carousel.GuessCarousel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GuessCarouselDaoService {
    @Autowired
    GuessCarouselRepository guessCarouselRepository;

    public GuessCarousel save(GuessCarousel guessCarousel) {
        return guessCarouselRepository.save(guessCarousel);
    }

    public void deleteById(String id) {
        guessCarouselRepository.deleteById(id);
    }

    public long countCarouselNum(){
        return guessCarouselRepository.count();
    }

    public List<GuessCarousel> findAll(){
        return guessCarouselRepository.findAll(Sort.by(Sort.Direction.ASC, "createTime"));
    }

    public GuessCarousel findById(String id){
        return guessCarouselRepository.findById(id).orElse(null);
    }
}
