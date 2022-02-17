package com.donglaistd.jinli.database.dao;

import com.donglaistd.jinli.database.entity.zone.DiaryReply;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class DiaryReplyDaoService {
    @Autowired
    DiaryReplyRepository diaryReplyRepository;
    @Autowired
    MongoTemplate mongoTemplate;

    public DiaryReply findById(String id){
        return diaryReplyRepository.findById(id);
    }

    public List<DiaryReply> findPageRootReply(String diaryId,int page,int rows,List<String> excludeReplyIds){
        if(Objects.isNull(excludeReplyIds)) excludeReplyIds = new ArrayList<>(0);
        Criteria criteria =new Criteria().andOperator( Criteria.where("diaryId").is(diaryId), Criteria.where("parentReplyId").is(null),Criteria.where("id").nin(excludeReplyIds));
        Pageable pageable = PageRequest.of(page, rows,Sort.by(Sort.Direction.DESC, "replyTime"));
        Query query = Query.query(criteria).with(pageable);
        return mongoTemplate.find(query, DiaryReply.class);
    }

    public List<DiaryReply> findAllSecondReply(String replyId,List<String> excludeReplyIds){
        if(Objects.isNull(excludeReplyIds)) excludeReplyIds = new ArrayList<>(0);
        Criteria criteria = new Criteria().andOperator(Criteria.where("parentReplyId").is(replyId),Criteria.where("id").nin(excludeReplyIds));
        Query query = Query.query(criteria).with(Sort.by(Sort.Direction.DESC, "replyTime"));
        return mongoTemplate.find(query, DiaryReply.class);
    }

    @Transactional
    public DiaryReply save(DiaryReply diaryReply) {
        return diaryReplyRepository.save(diaryReply);
    }

    public int queryReplyNum(String diaryId){
        Criteria criteria = Criteria.where("diaryId").is(diaryId);
        long count = mongoTemplate.count(Query.query(criteria), DiaryReply.class);
        return (int)count;
    }

    public int queryRootReplyNum(String diaryId){
        Criteria criteria =new Criteria().andOperator( Criteria.where("diaryId").is(diaryId), Criteria.where("parentReplyId").is(null));
        long count = mongoTemplate.count(Query.query(criteria), DiaryReply.class);
        return (int)count;
    }

    @Transactional
    public void deleteAllReplyByDiaryId(String diaryId){
        diaryReplyRepository.deleteAllByDiaryId(diaryId);
    }

}
