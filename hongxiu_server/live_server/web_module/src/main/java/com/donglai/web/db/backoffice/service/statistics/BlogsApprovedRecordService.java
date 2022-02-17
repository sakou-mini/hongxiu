package com.donglai.web.db.backoffice.service.statistics;

import com.donglai.web.db.backoffice.entity.BlogsApprovedRecord;
import com.donglai.web.db.backoffice.repository.statistics.BlogsApprovedRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BlogsApprovedRecordService {

    @Autowired
    BlogsApprovedRecordRepository blogsApprovedRecordRepository;

    public BlogsApprovedRecord getBlogsLastApprovedRecord(long blogsId) {
        List<BlogsApprovedRecord> records = blogsApprovedRecordRepository.findByBlogsIdOrderByApprovedTimeDesc(blogsId);
        return !records.isEmpty() ? records.get(0) : null;
    }

}
