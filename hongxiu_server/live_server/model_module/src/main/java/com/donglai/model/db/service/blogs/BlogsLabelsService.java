package com.donglai.model.db.service.blogs;

import com.donglai.model.db.entity.blogs.BlogsLabels;
import com.donglai.model.db.repository.blogs.BlogsLabelsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Moon
 * @date 2021-11-16 16:34
 */
@Service
public class BlogsLabelsService {
    @Autowired
    private BlogsLabelsRepository blogsReviewRepository;

    public BlogsLabels save(BlogsLabels blogs) {
        return blogsReviewRepository.save(blogs);
    }

    public List<Long> findByLabelsList(List<Integer> labels) {
        return blogsReviewRepository.findByLabelsIdIn(labels)
                .stream().map(BlogsLabels::getBlogsId).distinct().collect(Collectors.toList());
    }

    public List<BlogsLabels> findByLabelsId(Integer id) {
        return blogsReviewRepository.findByLabelsId(id);
    }
}
