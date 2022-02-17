package com.donglai.model.db.service.live;

import com.donglai.common.constant.BannerStatueEnum;
import com.donglai.model.db.entity.live.Banner;
import com.donglai.model.db.repository.live.BannerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Moon
 * @date 2021-12-20 16:19
 */
@Service
public class BannerService {

    @Autowired
    private BannerRepository bannerRepository;

    public Banner save(Banner banner) {
        return bannerRepository.save(banner);
    }


    public List<Banner> findAllByStatusIsFalseOrderBySortDesc() {
        return bannerRepository.findAllByStatusIsOrderBySortDesc(BannerStatueEnum.RUNNING.getValue());
    }

    public List<Banner> findByIdIn(List<Long> ids) {
        return bannerRepository.findByIdIn(ids);
    }

    public List<Banner> saveAll(List<Banner> byIdIn) {
        return bannerRepository.saveAll(byIdIn);
    }

    public Object deleteByIdIn(List<Long> ids) {
        return bannerRepository.deleteByIdIn(ids);
    }
}
