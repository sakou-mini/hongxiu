package com.donglaistd.jinli.database.dao.system.carousel;

import com.donglaistd.jinli.database.entity.system.carousel.GuessCarousel;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface GuessCarouselRepository extends MongoRepository<GuessCarousel,String> {
}
