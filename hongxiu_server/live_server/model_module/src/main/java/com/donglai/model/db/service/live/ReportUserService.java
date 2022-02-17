package com.donglai.model.db.service.live;

import com.donglai.model.db.entity.live.ReportUser;
import com.donglai.model.db.repository.live.ReportUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Moon
 * @date 2021-12-27 17:12
 */
@Service
public class ReportUserService {
    @Autowired
    private ReportUserRepository repository;


    public ReportUser save(ReportUser report) {
        return repository.save(report);
    }

    public List<ReportUser> saveAll(List<ReportUser> reports) {
        return repository.saveAll(reports);
    }

    public List<ReportUser> findByIds(List<Long> ids) {
        return repository.findByIdIn(ids);
    }

    public long count() {
        return repository.count();
    }
}
