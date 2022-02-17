package com.donglaistd.jinli.database.dao;

import com.donglaistd.jinli.database.entity.GiftOrder;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.http.entity.PageInfo;
import com.donglaistd.jinli.util.StringUtils;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class GiftOrderDaoService {
    @Autowired
    GiftOrderRepository giftOrderRepository;
    @Autowired
    UserDaoService userDaoService;
    @Autowired
    MongoTemplate mongoTemplate;

    public GiftOrder save(GiftOrder giftOrder){
        return giftOrderRepository.save(giftOrder);
    }

    public PageInfo<GiftOrder> pageQueryPlatformQGiftOrders(PageRequest pageRequest, Long startTime, Long endTime, String userId, String displayName) {
        Criteria criteria = new Criteria();
        List<Criteria> criteriaList = new ArrayList<>();
        if(Objects.nonNull(startTime)) criteriaList.add(Criteria.where("createTime").gte(startTime));
        if(Objects.nonNull(endTime)) criteriaList.add(Criteria.where("createTime").lte(endTime));
        if(!StringUtils.isNullOrBlank(userId)) {
            criteriaList.add(Criteria.where("senderId").is(userId));
        }else if(!StringUtils.isNullOrBlank(displayName)) {
            User user = userDaoService.findByDisplayName(displayName);
            if(Objects.isNull(user)) return new PageInfo<>(new ArrayList<>(), 0);
            criteriaList.add(Criteria.where("senderId").is(user.getId()));
        }
        if(!criteriaList.isEmpty()) criteria.andOperator(criteriaList.toArray(new Criteria[0]));
        Query query = Query.query(criteria);
        long total = mongoTemplate.count(query, GiftOrder.class);
        List<GiftOrder> platformRechargeRecords = mongoTemplate.find(query.with(pageRequest).with(Sort.by(Sort.Direction.DESC,"createTime")), GiftOrder.class);
        return new PageInfo<>(platformRechargeRecords, total);
    }

    public long totalGiftOrderByUserId(String userId){
        List<GiftOrder> userOrders = giftOrderRepository.findBySenderId(userId);
        if(userOrders == null) return 0;
        return userOrders.stream().mapToLong(GiftOrder::getTotalPrice).sum();
    }

    public long totalGiftOrderByTimes(long startTime, long endTime) {
        Aggregation aggregation = Aggregation.newAggregation(GiftOrder.class,
                Aggregation.match(Criteria.where("createTime").gte(startTime).lte(endTime)),
                Aggregation.group().sum("totalPrice").as("totalPrice"),
                Aggregation.project("totalPrice").andExclude("_id"));
        Document document = mongoTemplate.aggregate(aggregation, GiftOrder.class, Document.class).getUniqueMappedResult();
        return document == null ? 0 : document.getLong("totalPrice");
    }
}
