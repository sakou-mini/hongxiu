package com.donglai.statistics.db.service;

import com.donglai.model.db.entity.blogs.Blogs;
import com.donglai.model.db.entity.blogs.BlogsComment;
import com.donglai.model.db.entity.blogs.BlogsLike;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import static com.donglai.protocol.Constant.BlogsStatus.BLOGS_PASS;

@Service
public class BlogsTotalService {
    @Autowired
    MongoTemplate mongoTemplate;

    //统计指定时间内新增动态数
    public long countNewBlogsNumByTimeBetween(long startTime, long endTime) {
        Criteria criteria = Criteria.where("createAt").gte(startTime).lte(endTime).and("blogsStatus").is(BLOGS_PASS);
        return mongoTemplate.count(Query.query(criteria), Blogs.class);
    }

    //评论数
    public long countNewCommentNumByTimeBetween(long startTime, long endTime) {
        Criteria criteria = Criteria.where("commentTime").gte(startTime).lte(endTime).and("parentCommentId").is(null);
        return mongoTemplate.count(Query.query(criteria), BlogsComment.class);
    }

    public long countNewLikeNumByTimeBetween(long startTime, long endTime) {
        Criteria criteria = Criteria.where("time").gte(startTime).lte(endTime);
        return mongoTemplate.count(Query.query(criteria), BlogsLike.class);
    }
}
