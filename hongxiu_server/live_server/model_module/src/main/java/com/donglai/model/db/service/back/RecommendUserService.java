package com.donglai.model.db.service.back;

import com.donglai.model.db.entity.back.RecommendUser;
import com.donglai.model.db.repository.back.RecommendUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Moon
 * @date 2022-01-06 10:58
 */
@Service
public class RecommendUserService {

    @Autowired
    private RecommendUserRepository recommendUserRepository;

    public List<RecommendUser> findByIdIn(List<Long> ids) {
        return recommendUserRepository.findByIdIn(ids);
    }

    public List<RecommendUser> saveAll(List<RecommendUser> res) {
        return recommendUserRepository.saveAll(res);
    }

    public List<RecommendUser> deletedByIdIn(List<Long> ids) {
        return recommendUserRepository.deleteByIdIn(ids);
    }

    public RecommendUser save(RecommendUser recommendUser) {
        return recommendUserRepository.save(recommendUser);
    }

    public List<RecommendUser> findAll() {
        return recommendUserRepository.findAll();
    }
}
