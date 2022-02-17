package com.donglaistd.jinli.database.dao.system.carousel;

import com.donglaistd.jinli.database.entity.system.carousel.RaceCarousel;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RaceCarouselRepository extends MongoRepository<RaceCarousel,String> {
}
