package com.donglaistd.jinli.database.dao;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.entity.accusation.Report;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;


@Service
public class ReportDaoService {
    @Autowired
    private ReportRepository reportRepository;
    @Autowired
    MongoTemplate mongoTemplate;

    public Report findById(String id) {
        return reportRepository.findById(id);
    }

    @Transactional
    public Report save(Report report) {
        return reportRepository.save(report);
    }

    @Transactional
    public List<Report> saveAll(List<Report> list) {
        return reportRepository.saveAll(list);
    }

    public PageImpl<Report> findByFuzzyQueryReportList(String informantId, String reportedId, String reportedRoomId, Date startTime, Date endTime, boolean isHandle, int page, int size) {
        Criteria criteria = Criteria.where("isHandle").is(isHandle);
        Pageable thePage = PageRequest.of(page,size);
        List<Criteria> criteriaList = new ArrayList<>();
        if(Strings.isNotBlank(informantId)) criteriaList.add(Criteria.where("informantId").regex(informantId+".*"));
        if(Strings.isNotBlank(reportedId)) criteriaList.add(Criteria.where("reportedId").regex(reportedId+".*"));
        if(Strings.isNotBlank(reportedRoomId)) criteriaList.add(Criteria.where("reportedRoomId").regex(reportedRoomId+".*"));
        if(Objects.nonNull(startTime)) criteriaList.add(Criteria.where("createDate").gte(startTime));
        if(Objects.nonNull(endTime))  criteriaList.add(Criteria.where("createDate").lte(endTime));
        if(!criteriaList.isEmpty()) criteria = criteria.andOperator(criteriaList.toArray(new Criteria[criteriaList.size()]));
        Query query = Query.query(criteria).with(Sort.by(Sort.Direction.DESC,"createDate"));
        long count = mongoTemplate.count(query, Report.class);
        PageImpl<Report> reportData = new PageImpl<>(mongoTemplate.find(query.with(thePage), Report.class),PageRequest.of(page,size),count);
        return reportData;
//        return reportRepository.findByInformantIdLikeAndReportedIdLikeAndReportedRoomIdLikeAndIsHandle(informantId,reportedId,reportedRoomId,isHandle,thePage);
    }

    public List<Report> findAll (){
        return reportRepository.findAll();
    }

    public PageImpl<Report> findByFuzzyQueryReportListAndType(String informantId, String reportedId, String reportedRoomId, Constant.ViolationType violationType,boolean isHandle,Date startTime,Date endTime, int page, int size) {
//        Pageable thePage = PageRequest.of(page,size);
//        return reportRepository.findByInformantIdLikeAndReportedIdLikeAndReportedRoomIdLikeAndViolationTypeAndIsHandle(informantId,reportedId,reportedRoomId,violationType,isHandle,thePage);

        Criteria criteria = Criteria.where("violationType").is(violationType);
        Pageable thePage = PageRequest.of(page,size);
        List<Criteria> criteriaList = new ArrayList<>();
        if(Strings.isNotBlank(informantId)) criteriaList.add(Criteria.where("informantId").regex(informantId+".*"));
        if(Strings.isNotBlank(reportedId)) criteriaList.add(Criteria.where("reportedId").regex(reportedId+".*"));
        if(Strings.isNotBlank(reportedRoomId)) criteriaList.add(Criteria.where("reportedRoomId").regex(reportedRoomId+".*"));
        if(Objects.nonNull(startTime)) criteriaList.add(Criteria.where("createDate").gte(startTime));
        if(Objects.nonNull(endTime))  criteriaList.add(Criteria.where("createDate").lte(endTime));
        criteriaList.add(Criteria.where("isHandle").is(isHandle));
        if(!criteriaList.isEmpty()) criteria = criteria.andOperator(criteriaList.toArray(new Criteria[criteriaList.size()]));
        Query query = Query.query(criteria).with(Sort.by(Sort.Direction.DESC,"createDate"));
        long count = mongoTemplate.count(query, Report.class);
        PageImpl<Report> reportData = new PageImpl<>(mongoTemplate.find(query.with(thePage), Report.class),PageRequest.of(page,size),count);
        return reportData;

    }

//    public List<Report> findAllByPage(){
//        return reportRepository.find
//    }
}
