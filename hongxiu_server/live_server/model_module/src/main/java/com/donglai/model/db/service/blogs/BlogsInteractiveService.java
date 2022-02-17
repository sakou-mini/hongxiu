package com.donglai.model.db.service.blogs;

import com.donglai.model.db.entity.blogs.BlogsInteractive;
import com.donglai.model.db.repository.blogs.BlogsInteractiveRepository;
import com.donglai.protocol.Blog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Moon
 * @date 2022-01-11 10:41
 */
@Service
public class BlogsInteractiveService {


    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private BlogsInteractiveRepository blogRepository;

    public BlogsInteractive save(BlogsInteractive blogsInteractive) {
        return blogRepository.save(blogsInteractive);
    }

    public List<BlogsInteractive> findByToIdAndPagination(String userId, Blog.BlogsOfInteractiveListRequest request) {
        Criteria criteria = new Criteria();
        criteria.and("toId").is(userId);
        Pageable pageable = PageRequest.of(0, request.getSize());
        Query query = Query.query(criteria).with(Sort.by(Sort.Direction.DESC, "createdTime"));
        return mongoTemplate.find(query.with(pageable), BlogsInteractive.class);
    }

    public void deleteAll(List<BlogsInteractive> blogsInteractive) {
        blogRepository.deleteAll(blogsInteractive);
    }
}