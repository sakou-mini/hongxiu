package com.donglai.model.db.service.blogs;

import com.donglai.model.db.entity.blogs.BlogsReview;
import com.donglai.model.db.repository.blogs.BlogsReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Moon
 * @date 2021-11-11 15:36
 */
@Service
public class BlogsReviewService {
    @Autowired
    private BlogsReviewRepository blogsReviewRepository;

    /**
     * 新增更新
     *
     * @param blogs 对象
     * @return 返回对象
     */
    public BlogsReview save(BlogsReview blogs) {
        return blogsReviewRepository.save(blogs);
    }

    public List<BlogsReview> saveAll(List<BlogsReview> blogsReviewList) {
        return blogsReviewRepository.saveAll(blogsReviewList);
    }

    /**
     * 根据ID查找对象
     *
     * @param id 主键
     * @return 返回对象
     */
    public BlogsReview findById(long id) {
        return blogsReviewRepository.findById(id).orElse(null);
    }

    public void deleteById(long id) {
        blogsReviewRepository.deleteById(id);
    }
}
