package com.donglaistd.jinli.database.dao;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.entity.accusation.Report;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ReportRepository extends MongoRepository<Report, ObjectId> {
    Report findById(String id);

    List<Report> findByInformantIdLike(String informantId);

    Page<Report> findByInformantIdLikeAndReportedIdLikeAndReportedRoomIdLikeAndIsHandle(String informantId, String reportedId, String reportedRoomId,boolean isHandle,Pageable thePage);

    Page<Report> findByInformantIdLikeAndReportedIdLikeAndReportedRoomIdLikeAndViolationTypeAndIsHandle(String informantId, String reportedId, String reportedRoomId, Constant.ViolationType violationType,boolean isHandle, Pageable thePage);
}
