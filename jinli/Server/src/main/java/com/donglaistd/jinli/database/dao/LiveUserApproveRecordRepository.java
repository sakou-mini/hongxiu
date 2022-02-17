package com.donglaistd.jinli.database.dao;

import com.donglaistd.jinli.database.entity.backoffice.LiveUserApproveRecord;
import org.bson.types.ObjectId;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface LiveUserApproveRecordRepository extends PagingAndSortingRepository<LiveUserApproveRecord, String> {
    LiveUserApproveRecord findById(ObjectId id);

    List<LiveUserApproveRecord> findByApproveLiveUserId(String id);

    List<LiveUserApproveRecord> findByApproveState(boolean ApproveState);
}