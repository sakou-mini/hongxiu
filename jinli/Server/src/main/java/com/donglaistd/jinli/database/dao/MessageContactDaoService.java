package com.donglaistd.jinli.database.dao;

import com.donglaistd.jinli.database.entity.chat.MessageContact;
import com.mongodb.client.result.DeleteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
public class MessageContactDaoService {
    @Autowired
    MessageContactDaoService messageContactDaoService;
    @Autowired
    MessageContactRepository messageContactRepository;
    @Autowired
    MongoTemplate mongoTemplate;

    private Criteria getCriteriaFrom(String fromId, String toId)
    {
        return new Criteria().andOperator(Criteria.where("fromUid").is(fromId), Criteria.where("toUid").is(toId));
    }
    private Criteria getCriteriaTo(String fromId, String toId)
    {
        return new Criteria().andOperator(Criteria.where("fromUid").is(toId), Criteria.where("toUid").is(fromId));
    }

    public MessageContact updateMessageContact(String senderId,String receiverId){
        MessageContact oldRecord = findBySenderAndReceiver(senderId, receiverId);
        if (Objects.nonNull(oldRecord))
            messageContactDaoService.delete(oldRecord);
        MessageContact messageContact = MessageContact.newInstance(senderId, receiverId);
        return messageContactDaoService.save(messageContact);
    }

    @Transactional
    public void delete(MessageContact messageContact) {
       messageContactRepository.delete(messageContact);
    }

    public MessageContact save(MessageContact messageContact){
        return messageContactRepository.save(messageContact);
    }

    public MessageContact findBySenderAndReceiver(String senderId,String receiverId){
        return messageContactRepository.findBySenderIdAndReceiverId(senderId,receiverId);
    }

    public long deleteBySenderAndReceiver(String senderId,String receiverId){
        Criteria criteria = new Criteria().orOperator(getCriteriaFrom(senderId,receiverId),getCriteriaTo(senderId,receiverId));
        DeleteResult remove = mongoTemplate.remove(Query.query(criteria), MessageContact.class);
        return remove.getDeletedCount();
    }


    public Set<String> findUserNearestConcat(String userId,int num){
        Criteria criteria = new Criteria().orOperator(Criteria.where("receiverId").is(userId), Criteria.where("senderId").is(userId));
        Query query = Query.query(criteria).with(Sort.by(Sort.Direction.DESC, "time")).limit(num);
        List<MessageContact> messageRecords = mongoTemplate.find(query, MessageContact.class);
        return concatList(messageRecords,userId);
    }

    private Set<String> concatList( List<MessageContact> concatList,String userId){
        Set<String> allConcat = new HashSet<>(concatList.size());
        String receiverId;
        String senderId;
        for (MessageContact contact : concatList) {
            receiverId = contact.getReceiverId();
            senderId = contact.getSenderId();
            if(Objects.equals(receiverId,userId)) allConcat.add(receiverId);
            if(Objects.equals(senderId,userId)) allConcat.add(senderId);
        }
        return allConcat;
    }
}
