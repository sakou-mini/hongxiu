package com.donglai.model.db.service.back;

import com.donglai.model.db.entity.back.RecommendVideo;
import com.donglai.model.db.repository.back.RecommendVideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Moon
 * @date 2022-01-05 16:23
 */
@Service
public class RecommendVideoService {

    @Autowired
    private RecommendVideoRepository recommendVideoRepository;

    public List<RecommendVideo> findByIdIn(List<Long> ids) {
        return recommendVideoRepository.findByIdIn(ids);
    }

    public List<RecommendVideo> saveAll(List<RecommendVideo> res) {
        return recommendVideoRepository.saveAll(res);
    }


    public List<RecommendVideo> deletedByIdIn(List<Long> ids) {
        return recommendVideoRepository.deleteByIdIn(ids);
    }


    public RecommendVideo save(RecommendVideo recommendVideo) {
        return recommendVideoRepository.save(recommendVideo);
    }

    public List<RecommendVideo> findAll(){
        return recommendVideoRepository.findAll();
    }
}
