package com.donglai.model.db.service.live;

import com.donglai.model.db.entity.live.ReportVideo;
import com.donglai.model.db.repository.live.ReportVideoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Moon
 * @date 2021-12-27 17:11
 */
@Service
public class ReportVideoService {
    @Autowired
    private ReportVideoRepository repository;


    public ReportVideo save(ReportVideo report) {
        return repository.save(report);
    }

    public List<ReportVideo> saveAll(List<ReportVideo> reports) {
        return repository.saveAll(reports);
    }

    public List<ReportVideo> findByIds(List<Long> ids) {
        return repository.findByIdIn(ids);
    }

    public long count(){
        return repository.count();
    }
}
