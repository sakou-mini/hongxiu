package com.donglaistd.jinli.database.dao;

import com.donglaistd.jinli.database.entity.chat.MessageRecord;
import com.donglaistd.jinli.util.TimeUtil;
import com.mongodb.client.result.DeleteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.donglaistd.jinli.constant.GameConstant.MESSAGE_OVERDAY;

@Service
public class MessageRecordDaoService {
    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    MessageRecordRepository messageRecordRepository;

    private Criteria getCriteriaFrom(String fromId, String toId) {
        return new Criteria().andOperator(Criteria.where("fromUid").is(fromId), Criteria.where("toUid").is(toId));
    }

    private Criteria getCriteriaTo(String fromId, String toId) {
        return new Criteria().andOperator(Criteria.where("fromUid").is(toId), Criteria.where("toUid").is(fromId));
    }

    @Transactional
    public List<MessageRecord> findListByFromAndTo(String fromId, String toId){
        Criteria criteria = new Criteria().orOperator(getCriteriaFrom(fromId,toId));
        Query query = Query.query(criteria).with(Sort.by(Sort.Direction.DESC, "sendTime"));
        List<MessageRecord> messageRecords = mongoTemplate.find(query, MessageRecord.class);
        deleteAll(messageRecords);
        return messageRecords;
    }

    public MessageRecord saveMessage(MessageRecord message){
        return messageRecordRepository.save(message);
    }

    public long deleteMessageByFromAndTo(String fromId, String toId) {
        Criteria criteria = new Criteria().orOperator(getCriteriaFrom(fromId,toId), getCriteriaTo(fromId,toId));
        DeleteResult remove = mongoTemplate.remove(Query.query(criteria), MessageRecord.class);
        return remove.getDeletedCount();
    }

    public void deleteAll(List<MessageRecord> messageRecords){
        messageRecordRepository.deleteAll(messageRecords);
    }

    public long cleanOverTimeMessageRecord(){
        long beforeDayStartTime = TimeUtil.getBeforeDayStartTime(MESSAGE_OVERDAY);
        Criteria criteria = new Criteria().andOperator(Criteria.where("sendTime").lte(beforeDayStartTime));
        return mongoTemplate.remove(Query.query(criteria), MessageRecord.class).getDeletedCount();
    }

    public long countChatHistoryBySenderAndReceiver(String fromId,String toId){
        Criteria criteria = new Criteria().orOperator(getCriteriaFrom(fromId,toId), getCriteriaTo(fromId,toId));
        return mongoTemplate.count(Query.query(criteria), MessageRecord.class);
    }

    public List<MessageRecord> saveAll(List<MessageRecord> messageRecords){
        return messageRecordRepository.saveAll(messageRecords);
    }

    public List<MessageRecord> findAllUnReadMessage(String toUserId) {
        return messageRecordRepository.findByToUidIsAndRead(toUserId, false);
    }
}
