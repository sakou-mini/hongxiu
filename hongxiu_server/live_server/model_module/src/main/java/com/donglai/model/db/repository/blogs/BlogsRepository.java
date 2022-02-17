package com.donglai.model.db.repository.blogs;

import com.donglai.model.db.entity.blogs.Blogs;
import com.donglai.protocol.Constant;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Set;

public interface BlogsRepository extends MongoRepository<Blogs, Long> {
    List<Blogs> findAllByUserIdOrderByCreateAtDesc(String userId);

    List<Blogs> findAllByUserIdAndBlogsStatusIs(String userId, Constant.BlogsStatus status);

    long countByUserId(String userId);

    long countByUserIdAndBlogsStatusIs(String userId, Constant.BlogsStatus status);

    List<Blogs> findAllByBlogsStatusIs(Constant.BlogsStatus status);

    List<Blogs> findByUserIdInAndBlogsStatusIsOrderByCreateAt(Set<String> userId, Constant.BlogsStatus status);

    List<Blogs> findByBlogsTypeAndBlogsStatus(Constant.BlogsType type, Constant.BlogsStatus status);

    List<Blogs> findByBlogsStatusAndUserIdInOrderByCreateAtDesc(Constant.BlogsStatus status, List<String> userIds);

    List<Blogs> findByIdInAndBlogsStatusIs(List<Long> ids, Constant.BlogsStatus blogsPass);
}
