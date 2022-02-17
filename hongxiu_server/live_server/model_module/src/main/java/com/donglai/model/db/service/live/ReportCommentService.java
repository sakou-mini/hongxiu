package com.donglai.model.db.service.live;

import com.donglai.model.db.entity.live.ReportComment;
import com.donglai.model.db.repository.live.ReportCommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Moon
 * @date 2021-12-27 17:12
 */
@Service
public class ReportCommentService {
    @Autowired
    private ReportCommentRepository repository;


    public ReportComment save(ReportComment reportComment) {
        return repository.save(reportComment);
    }

    public List<ReportComment> saveAll(List<ReportComment> reportComments) {
        return repository.saveAll(reportComments);
    }

    public List<ReportComment> findByIds(List<Long> ids) {
        return repository.findByIdIn(ids);
    }

    public long count() {
        return repository.count();
    }
}
