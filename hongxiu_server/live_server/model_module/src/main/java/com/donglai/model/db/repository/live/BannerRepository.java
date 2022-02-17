package com.donglai.model.db.repository.live;

import com.donglai.model.db.entity.live.Banner;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * @author Moon
 * @date 2021-12-20 16:20
 */
public interface BannerRepository extends MongoRepository<Banner, String> {

    List<Banner> findAllByStatusIsOrderBySortDesc(int statue);

    List<Banner> findByIdIn(List<Long> ids);

    List<Banner> deleteByIdIn(List<Long> ids);
}
