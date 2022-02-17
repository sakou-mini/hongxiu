package com.donglai.model.db.service.live;

import com.donglai.model.db.entity.live.FeedBack;
import com.donglai.model.db.repository.live.FeedBackRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Moon
 * @date 2021-12-28 17:52
 */
@Service
public class FeedBackService {
    @Autowired
    private FeedBackRepository feedBackRepository;

    public FeedBack findById(Long id) {
        return feedBackRepository.findById(id).orElse(null);
    }

    public FeedBack save(FeedBack feedback) {
        return feedBackRepository.save(feedback);
    }

    public List<FeedBack> deleteByIdIn(List<Long> ids) {
        return feedBackRepository.deleteByIdIn(ids);
    }

    public long count(){
        return feedBackRepository.count();
    }

    public List<FeedBack> saveAll(List<FeedBack> feedBackList) {
        return feedBackRepository.saveAll(feedBackList);
    }
}
