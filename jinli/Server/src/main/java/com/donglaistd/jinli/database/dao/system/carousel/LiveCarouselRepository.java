package com.donglaistd.jinli.database.dao.system.carousel;

import com.donglaistd.jinli.database.entity.system.carousel.LiveCarousel;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LiveCarouselRepository extends MongoRepository<LiveCarousel,String>{
}
