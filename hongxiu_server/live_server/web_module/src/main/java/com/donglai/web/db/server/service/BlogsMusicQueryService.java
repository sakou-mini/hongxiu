package com.donglai.web.db.server.service;

import com.donglai.common.util.StringUtils;
import com.donglai.model.db.entity.blogs.BlogsMusic;
import com.donglai.model.db.service.blogs.BlogsMusicService;
import com.donglai.web.response.PageInfo;
import com.donglai.web.web.dto.request.MusicListRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.donglai.web.constant.DataBaseFiledConstant.*;

@Component
public class BlogsMusicQueryService {
    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    BlogsMusicService blogsMusicService;

    public PageInfo<BlogsMusic> queryBlogsMusicList(MusicListRequest request) {
        Criteria criteria = new Criteria();
        if (!StringUtils.isNullOrBlank(request.getMusicName()))
            criteria.and(MUSIC_NAME).is(request.getMusicName());
        if (!StringUtils.isNullOrBlank(request.getMusicAuthor()))
            criteria.and(MUSIC_AUTHOR).is(request.getMusicAuthor());
        if (request.getStatus() >= 0)
            criteria.and(MUSIC_STATUS).is(request.getStatus() == 1);
        CommonQueryService.getCriteriaByTimes(request.getStartTime(), request.getEndTime(), criteria, MUSIC_CREATETIME);
        Query query = Query.query(criteria);
        long totalNum = mongoTemplate.count(query, BlogsMusic.class);
        PageRequest pageRequest = PageRequest.of(request.getPage() <= 0 ? 0 : request.getPage() - 1, request.getSize(), Sort.by(Sort.Direction.DESC, MUSIC_CREATETIME));
        List<BlogsMusic> blogsMusics = mongoTemplate.find(query.with(pageRequest).with(Sort.by(Sort.Direction.DESC, "createTime")), BlogsMusic.class);
        return new PageInfo<>(pageRequest, blogsMusics, totalNum);
    }

    public long countMusicUsedNum(long musicId) {
        return blogsMusicService.countMusicUsedNum(musicId);
    }
}
