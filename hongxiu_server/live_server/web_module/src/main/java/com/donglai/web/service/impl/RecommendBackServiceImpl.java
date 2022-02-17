package com.donglai.web.service.impl;

import com.donglai.common.util.CombineBeansUtil;
import com.donglai.model.db.entity.back.RecommendUser;
import com.donglai.model.db.entity.back.RecommendVideo;
import com.donglai.model.db.entity.blogs.Blogs;
import com.donglai.model.db.entity.common.User;
import com.donglai.model.db.service.back.RecommendUserService;
import com.donglai.model.db.service.back.RecommendVideoService;
import com.donglai.model.db.service.blogs.BlogsService;
import com.donglai.model.db.service.common.UserService;
import com.donglai.protocol.Constant;
import com.donglai.web.db.backoffice.entity.BackOfficeUser;
import com.donglai.web.db.backoffice.service.BackOfficeUserService;
import com.donglai.web.response.*;
import com.donglai.web.service.RecommendBackService;
import com.donglai.web.web.dto.request.RecommendFindUserListRequest;
import com.donglai.web.web.dto.request.RecommendFindVideoListRequest;
import com.donglai.web.web.dto.request.RecommendUpdateStatusRequest;
import com.donglai.web.web.vo.RecommendUserVO;
import com.donglai.web.web.vo.RecommendVideoVO;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Moon
 * @date 2022-01-05 13:49
 */
@Service
public class RecommendBackServiceImpl implements RecommendBackService {

    @Autowired
    private RecommendUserService recommendUserService;
    @Autowired
    private UserService userService;
    @Autowired
    private BlogsService blogsService;
    @Autowired
    private RecommendVideoService recommendVideoService;
    @Autowired
    private BackOfficeUserService backOfficeUserService;


    @Autowired
    private com.donglai.web.db.server.service.RecommendDbService recommendDbService;

    @Override
    public RestResponse updateVideoStatus(RecommendUpdateStatusRequest request) {
        if (CollectionUtils.isEmpty(request.getIds()) || Objects.isNull(request.getStatus())) {
            return new ErrorResponse(GlobalResponseCode.PARAM_ERROR);
        }
        List<RecommendVideo> res = recommendVideoService.findByIdIn(request.getIds());

        return new SuccessResponse().withData(recommendVideoService.saveAll(res));
    }

    @Override
    public RestResponse addUser(String userId, String log) {
        if (userId == null) {
            return new ErrorResponse(GlobalResponseCode.PARAM_ERROR);
        }

        User user = userService.findById(userId);
        BackOfficeUser principal = (BackOfficeUser) SecurityUtils.getSubject().getPrincipal();

        RecommendUser recommendUser = new RecommendUser();
        recommendUser.setCreatedTime(System.currentTimeMillis());
        recommendUser.setUpdatedTime(System.currentTimeMillis());
        recommendUser.setJoinTime(user.getCreateTime());
        recommendUser.setUserId(userId);
        recommendUser.setCreateId(principal.getId());
        recommendUser.setTxt(log);

        return new SuccessResponse().withData(recommendUserService.save(recommendUser));
    }

    @Override
    public RestResponse addVideo(Long blogId) {
        if (blogId == null) {
            return new ErrorResponse(GlobalResponseCode.PARAM_ERROR);
        }
        Blogs byId = blogsService.findById(blogId);
        if(byId.getBlogsStatus().equals(Constant.BlogsStatus.BLOGS_PASS)){
            BackOfficeUser principal = (BackOfficeUser) SecurityUtils.getSubject().getPrincipal();

            RecommendVideo recommendVideo = new RecommendVideo();
            recommendVideo.setBlogsId(blogId);
            recommendVideo.setBlogUserId(byId.getUserId());
            recommendVideo.setCreatedId(principal.getId());
            recommendVideo.setCreatedTime(System.currentTimeMillis());
            recommendVideo.setUpdatedTime(System.currentTimeMillis());

            return new SuccessResponse().withData(recommendVideoService.save(recommendVideo));
        }

        return new ErrorResponse(GlobalResponseCode.PARAM_ERROR.getCode(),"该视频审核未通过");
    }

    @Override
    public RestResponse deletedUser(RecommendUpdateStatusRequest request) {
        return new SuccessResponse().withData(recommendUserService.deletedByIdIn(request.getIds()));
    }

    @Override
    public RestResponse updateUserStatus(RecommendUpdateStatusRequest request) {
        if (CollectionUtils.isEmpty(request.getIds()) || Objects.isNull(request.getStatus())) {
            return new ErrorResponse(GlobalResponseCode.PARAM_ERROR);
        }
        List<RecommendUser> res = recommendUserService.findByIdIn(request.getIds());

        return new SuccessResponse().withData(recommendUserService.saveAll(res));
    }

    @Override
    public PageInfo<RecommendUserVO> findUserList(RecommendFindUserListRequest request) {
        PageInfo<RecommendUser> pageRes = recommendDbService.conditionGetRecommendUserList(request);
        List<RecommendUser> content = pageRes.getContent();
        List<RecommendUserVO> recommendUsers = content.stream().map(userVo -> new CombineBeansUtil<>(RecommendUserVO.class).combineBeans(userVo)).collect(Collectors.toList());
        recommendUsers.forEach(v -> {
            BackOfficeUser backUser = backOfficeUserService.findById(v.getCreateId());
            User user = userService.findById(v.getUserId());
            v.setCreateId(backUser.getNickname());
            v.setNickname(user.getNickname());
        });

        return new PageInfo<>(pageRes.getPageNum(), pageRes.getPageSize(), pageRes.getTotal(), recommendUsers);

    }

    @Override
    public RestResponse deletedVideo(RecommendUpdateStatusRequest request) {
        return new SuccessResponse().withData(recommendVideoService.deletedByIdIn(request.getIds()));
    }

    @Override
    public PageInfo<RecommendVideoVO> findVideoList(RecommendFindVideoListRequest request) {
        PageInfo<RecommendVideo> pageRes = recommendDbService.conditionGetRecommendVideodList(request);
        List<RecommendVideo> content = pageRes.getContent();
        List<RecommendVideoVO> vos = new ArrayList<>();
        content.forEach(v -> {
            RecommendVideoVO vo = new RecommendVideoVO();

            Blogs byId = blogsService.findById(v.getBlogsId());
            BackOfficeUser backOfficeUser = backOfficeUserService.findById(v.getCreatedId());
            User user = userService.findById(v.getBlogUserId());
            vo.setId(v.getId());
            vo.setUrl(byId.getResourceUrl());
            vo.setContent(byId.getContent());
            vo.setBlogCreateName(user.getNickname());
            vo.setBlogUserId(v.getBlogUserId());
            vo.setCreatedTime(v.getCreatedTime());
            vo.setUpdatedTime(v.getUpdatedTime());
            vo.setCreatedId(backOfficeUser.getNickname());
            vos.add(vo);
        });

        return new PageInfo<>(pageRes.getPageNum(), pageRes.getPageSize(), pageRes.getTotal(), vos);

    }
}