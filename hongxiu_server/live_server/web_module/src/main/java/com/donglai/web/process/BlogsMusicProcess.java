package com.donglai.web.process;

import com.donglai.common.util.CombineBeansUtil;
import com.donglai.common.util.StringUtils;
import com.donglai.model.db.entity.blogs.BlogsMusic;
import com.donglai.model.db.service.blogs.BlogsMusicService;
import com.donglai.web.db.backoffice.entity.BackOfficeUser;
import com.donglai.web.db.backoffice.service.BackOfficeUserService;
import com.donglai.web.db.server.service.BlogsMusicQueryService;
import com.donglai.web.response.GlobalResponseCode;
import com.donglai.web.response.PageInfo;
import com.donglai.web.web.dto.reply.BlogsMusicListReply;
import com.donglai.web.web.dto.request.AddMusicRequest;
import com.donglai.web.web.dto.request.ApprovalRequest;
import com.donglai.web.web.dto.request.EditMusicRequest;
import com.donglai.web.web.dto.request.MusicListRequest;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.donglai.web.response.GlobalResponseCode.*;

@Component
public class BlogsMusicProcess {
    @Autowired
    BlogsMusicQueryService blogsMusicQueryService;
    @Autowired
    BackOfficeUserService backOfficeUserService;
    @Autowired
    BlogsMusicService blogsMusicService;

    public PageInfo<BlogsMusicListReply> queryMusicList(MusicListRequest request) {
        PageInfo<BlogsMusic> blogsMusicPageInfo = blogsMusicQueryService.queryBlogsMusicList(request);
        List<BlogsMusicListReply> content = blogsMusicPageInfo.getContent().stream().map(this::buildBlogsMusicListReply).collect(Collectors.toList());
        return new PageInfo<>(PageRequest.of(request.getPage(), request.getSize()), content, blogsMusicPageInfo.getTotal());
    }

    private BlogsMusicListReply buildBlogsMusicListReply(BlogsMusic blogsMusic) {
        BlogsMusicListReply musicListReply = new CombineBeansUtil<>(BlogsMusicListReply.class).combineBeans(blogsMusic);
        if (!StringUtils.isNullOrBlank(blogsMusic.getBackofficeUserId())) {
            musicListReply.setOperatorName(Optional.ofNullable(backOfficeUserService.findById(blogsMusic.getBackofficeUserId())).orElse(BackOfficeUser.newEmptyBackOfficeUser()).getNickname());
        }
        musicListReply.setUsedCount(blogsMusicQueryService.countMusicUsedNum(blogsMusic.getId()));
        return musicListReply;
    }

    public GlobalResponseCode auditMusic(ApprovalRequest request) {
        var backOfficeUser = (BackOfficeUser) SecurityUtils.getSubject().getPrincipal();
        boolean pass = request.getStatus() == 1;
        List<Long> ids = request.getIds();
        List<BlogsMusic> musics = blogsMusicService.findByIds(ids);
        if (musics.size() != ids.size()) return MUSIC_NOT_EXIT;
        for (BlogsMusic music : musics) {
            music.auditMusic(pass, backOfficeUser.getId());
        }
        blogsMusicService.saveAll(musics);
        return SUCCESS;
    }

    public GlobalResponseCode addMusic(AddMusicRequest request) {
        var backOfficeUser = (BackOfficeUser) SecurityUtils.getSubject().getPrincipal();
        if (StringUtils.isNullOrBlank(request.getMusicUrl()) || StringUtils.isNullOrBlank(request.getMusicName()) || StringUtils.isNullOrBlank(request.getMusicAuthor())) {
            return MUSIC_PARAM_ERROR;
        } else {
            //todo 客户端暂未传递音乐时长
            BlogsMusic blogsMusic = BlogsMusic.newInstance(request.getMusicName(), request.getMusicUrl(), request.getMusicAuthor(), request.getMusicCoverUrl(), true, 0);
            blogsMusic.setBackofficeUserId(backOfficeUser.getId());
            blogsMusicService.save(blogsMusic);
            return SUCCESS;
        }
    }

    public GlobalResponseCode delMusic(long musicId) {
        BlogsMusic blogsMusic = blogsMusicService.findById(musicId);
        if(Objects.isNull(blogsMusic)){
            return MUSIC_NOT_EXIT;
        }else{
            blogsMusicService.deleteMusic(blogsMusic);
            return SUCCESS;
        }
    }

    public GlobalResponseCode editMusic(EditMusicRequest request) {
        BlogsMusic blogsMusic = blogsMusicService.findById(request.getMusicId());
        if(Objects.isNull(blogsMusic)){
            return MUSIC_NOT_EXIT;
        }else{
            blogsMusic.setMusicName(request.getMusicName());
            blogsMusic.setMusicAuthor(request.getMusicAuthor());
            blogsMusic.setUpdateTime(System.currentTimeMillis());
            blogsMusicService.save(blogsMusic);
            return SUCCESS;
        }
    }
}
