package com.donglai.web.db.backoffice.repository.statistics;

import com.donglai.web.db.backoffice.entity.BlogsApprovedRecord;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface BlogsApprovedRecordRepository extends MongoRepository<BlogsApprovedRecord, ObjectId> {
    List<BlogsApprovedRecord> findByBlogsIdOrderByApprovedTimeDesc(long blogsId);
}
