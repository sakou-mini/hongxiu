package com.donglai.web.process;

import com.alibaba.fastjson.JSONArray;
import com.donglai.common.util.StringUtils;
import com.donglai.model.db.entity.blogs.Blogs;
import com.donglai.model.db.entity.blogs.BlogsComment;
import com.donglai.model.db.entity.blogs.BlogsLabelsConfig;
import com.donglai.model.db.entity.blogs.BlogsReview;
import com.donglai.model.db.entity.common.User;
import com.donglai.model.db.service.blogs.BlogsCommentService;
import com.donglai.model.db.service.blogs.BlogsLabelsConfigService;
import com.donglai.model.db.service.blogs.BlogsReviewService;
import com.donglai.model.db.service.blogs.BlogsService;
import com.donglai.model.db.service.common.FollowListService;
import com.donglai.model.db.service.common.UserService;
import com.donglai.protocol.Constant;
import com.donglai.web.config.LabelJsonConfig;
import com.donglai.web.db.backoffice.entity.BackOfficeUser;
import com.donglai.web.db.backoffice.service.BackOfficeUserService;
import com.donglai.web.db.backoffice.service.statistics.BlogsApprovedRecordService;
import com.donglai.web.db.server.service.BlogsCommentQueryService;
import com.donglai.web.db.server.service.BlogsQueryService;
import com.donglai.web.response.*;
import com.donglai.web.web.dto.reply.BlogsCommentListReply;
import com.donglai.web.web.dto.reply.BlogsDetailReply;
import com.donglai.web.web.dto.reply.BlogsListReply;
import com.donglai.web.web.dto.request.ApprovalRequest;
import com.donglai.web.web.dto.request.BlogsOrCommentListRequest;
import com.donglai.web.web.dto.request.CreateBlogsRequest;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

import static com.donglai.common.constant.SystemConstant.OFFICIAL_ACCOUNT;
import static com.donglai.protocol.Constant.BlogsType.BLOGS_IMAGE;
import static com.donglai.protocol.Constant.ResultCode.*;

@Component
@Slf4j
public class BlogsProcess {
    @Value("${blog.image.max.num}")
    private int imageBlogMaxNum;
    @Value("${blog.video.max.num}")
    private int videoBlogMaxNum;
    @Autowired
    UserService userService;
    @Autowired
    BlogsService blogsService;
    @Autowired
    BlogsQueryService blogsQueryService;
    @Autowired
    BlogsCommentQueryService blogsCommentQueryService;
    @Autowired
    BlogsApprovedRecordService blogsApprovedRecordService;
    @Autowired
    BackOfficeUserService backOfficeUserService;
    @Autowired
    private FollowListService followListService;
    @Autowired
    private BlogsReviewService blogsReviewService;
    @Autowired
    BlogsCommentService blogsCommentService;


    //==============createBlogs==============
    private boolean verifyTargetResourceNum(int resourceNum, Constant.BlogsType type) {
        if (resourceNum <= 0) {
            log.warn("blogs resource is empty");
            return false;
        }
        if (Objects.equals(BLOGS_IMAGE, type)) return resourceNum <= imageBlogMaxNum;
        else return resourceNum <= videoBlogMaxNum;
    }

    public Constant.ResultCode createBlogs(CreateBlogsRequest createBlogsRequest) {
        if (StringUtils.isNullOrBlank(createBlogsRequest.getContent()) || createBlogsRequest.getBlogsType() == null) {
            log.warn("blogs_content_empty_or_too_long");
            return BLOGS_PARAM_MISSING;
        } else if (Objects.equals(Constant.BlogsType.BLOGS_VIDEO, createBlogsRequest.getBlogsType()) && createBlogsRequest.getThumbnails().size() < 1) {
            log.warn("blogs_thumbnail_empty");
            return BLOGS_THUMBNAIL_EMPTY;
        } else if (!verifyTargetResourceNum(createBlogsRequest.getResourceUrl().size(), createBlogsRequest.getBlogsType())) {
            log.warn("blogs_resource_over_limit");
            return BLOGS_RESOURCE_OVER_LIMIT;
        } else {
            User user = userService.findByAccountId(OFFICIAL_ACCOUNT);
            if (user == null) {
                log.error("not fount official user");
                return OFFICIAL_USER_NOT_EXIT;
            } else {
                Blogs blogs = Blogs.newInstance(user.getId(), createBlogsRequest.getContent(), Constant.BlogsStatus.BLOGS_PASS, createBlogsRequest.getBlogsType(), createBlogsRequest.getThumbnails());
                blogs.setResourceUrl(createBlogsRequest.getResourceUrl());
                blogsService.save(blogs);
                return SUCCESS;
            }
        }
    }

    //=============get Blogs pageList================
    public PageInfo<BlogsListReply> queryBlogsByBlogsListRequest(BlogsOrCommentListRequest request) {
        PageInfo<Blogs> blogsPageInfo = blogsQueryService.queryBlogsByBlogsListRequest(request);
        List<Blogs> contents = blogsPageInfo.getContent();
        List<BlogsListReply> blogsList = new ArrayList<>(contents.size());
        contents.forEach(blogs -> blogsList.add(buildBlogsListReply(blogs)));
        return new PageInfo<>(blogsPageInfo.getPageNum(), blogsPageInfo.getPageSize(), blogsPageInfo.getTotal(), blogsList);
    }

    private BlogsListReply buildBlogsListReply(Blogs blogs) {
        BlogsReview blogsReview = Optional.ofNullable(blogsReviewService.findById(blogs.getId())).orElse(new BlogsReview(blogs.getId(), blogs.getUserId()));
        //查找标签名
        List<String> labels = blogsLabelsConfigService.findByLabelsIdIn(Lists.newArrayList(blogs.getLabels())).stream().map(BlogsLabelsConfig::getLabelName).collect(Collectors.toList());
        BlogsListReply blogsListReply = new BlogsListReply(blogs, blogsReview, labels);
        if (!StringUtils.isNullOrBlank(blogsReview.getBackofficeUserId())) {
            blogsListReply.setOperatorName(Optional.ofNullable(backOfficeUserService.findById(blogsReview.getBackofficeUserId())).orElse(BackOfficeUser.newEmptyBackOfficeUser()).getNickname());
        }
        blogsListReply.setNickname(Optional.ofNullable(userService.findById(blogs.getUserId())).orElse(new User()).getNickname());
        return blogsListReply;
    }

    //==================审核动态=======================
    //人工审核动态
    public GlobalResponseCode manualApprovalBlogs(ApprovalRequest request) {
        BackOfficeUser backOfficeUser = (BackOfficeUser) SecurityUtils.getSubject().getPrincipal();
        Constant.BlogsStatus blogsStatus = Constant.BlogsStatus.forNumber(request.getStatus());
        if (blogsStatus == null) return GlobalResponseCode.BLOGS_APPROVAL_STATUS_ERROR;
        List<Long> blogsIds = request.getIds();
        List<Blogs> blogsList = blogsIds.stream().map(id -> blogsService.findById(id)).filter(Objects::nonNull).collect(Collectors.toList());
        if (blogsList.size() != blogsIds.size()) return GlobalResponseCode.BLOGS_NOT_EXIST;
        List<BlogsReview> blogsReviewList = new ArrayList<>();
        BlogsReview blogsReview;
        for (Blogs blogs : blogsList) {
            blogsReview = Optional.ofNullable(blogsReviewService.findById(blogs.getId())).orElse(new BlogsReview(blogs.getId(), blogs.getUserId()));
            //人工审核
            blogsReview.manualAudit(blogsStatus, backOfficeUser.getId());
            blogs.audit(blogsStatus);
            //审核通过
            if (Objects.equals(blogsStatus, Constant.BlogsStatus.BLOGS_PASS)) {
                //添加粉丝列表new标签
                followListService.updateUserFollowersNewTagByLeaderId(blogs.getUserId());
            } else if (Objects.equals(blogsStatus, Constant.BlogsStatus.BLOGS_NO_PASS)) {
                blogsReview.setRefuseReason(request.getRefuseReason());
            }
            blogsReviewList.add(blogsReview);
        }
        blogsService.saveAll(blogsList);
        blogsReviewService.saveAll(blogsReviewList);
        return GlobalResponseCode.SUCCESS;
    }

    /**
     * @param blogs        动态
     * @param refuseReason 失败原因
     */
    public void systemAuditBlogsNoPass(Blogs blogs, String refuseReason) {
        blogs.audit(Constant.BlogsStatus.BLOGS_NO_PASS);
        BlogsReview blogsReview = Optional.ofNullable(blogsReviewService.findById(blogs.getId())).orElse(new BlogsReview());
        blogs.setFailNum(blogs.getFailNum() + 1);
        blogsReview.setId(blogs.getId());
        blogsReview.setRefuseReason(refuseReason);
        blogsReview.setUserId(blogs.getUserId());
        blogsReview.setResourceUrl(blogs.getResourceUrl().get(0));
        blogsReview.systemAudit(Constant.BlogsStatus.BLOGS_NO_PASS);
    }

    /**
     * @param blogs  动态
     * @param labels 标签
     */
    public void systemAuditBlogsPass(Blogs blogs, Set<Integer> labels) {
        blogs.audit(Constant.BlogsStatus.BLOGS_PASS);
        blogs.setLabels(labels);
        //审核记录
        BlogsReview blogsReview = Optional.ofNullable(blogsReviewService.findById(blogs.getId())).orElse(new BlogsReview());
        blogsReview.setId(blogs.getId());
        blogsReview.setUserId(blogs.getUserId());
        blogsReview.setResourceUrl(blogs.getResourceUrl().get(0));
        blogsReview.systemAudit(Constant.BlogsStatus.BLOGS_PASS);
        blogsReviewService.save(blogsReview);
        blogsService.save(blogs);
        blogsReviewService.save(blogsReview);
    }

    public BlogsDetailReply blogsDetail(long blogsId) {
        Blogs blogs = blogsService.findById(blogsId);
        if (blogs == null) return new BlogsDetailReply();
        User user = userService.findById(blogs.getUserId());
        BlogsReview blogsReview = blogsReviewService.findById(blogsId);
        BackOfficeUser approvedBackOfficeUser = null;
        if (blogsReview != null && !StringUtils.isNullOrBlank(blogsReview.getBackofficeUserId())) {
            approvedBackOfficeUser = backOfficeUserService.findById(blogsReview.getBackofficeUserId());
        }
        return new BlogsDetailReply(blogs, user, approvedBackOfficeUser);
    }

    public GlobalResponseCode modifyBlog(long blogsId, String content) {
        Blogs blogs = blogsService.findById(blogsId);
        if (Objects.isNull(blogs)) return GlobalResponseCode.BLOGS_NOT_EXIST;
        blogs.setContent(content);
        //此处可能要求动态服 要定时更新索引库。
        blogsService.save(blogs);
        return GlobalResponseCode.SUCCESS;
    }

    //=======================BlogComment==============================
    public PageInfo<BlogsCommentListReply> queryCommentList(BlogsOrCommentListRequest request) {
        PageInfo<BlogsComment> blogsCommentPageInfo = blogsCommentQueryService.queryCommentListByPage(request);
        List<BlogsCommentListReply> blogsCommentList = blogsCommentPageInfo.getContent().stream().map(this::buildBlogsCommentListReply).collect(Collectors.toList());
        return new PageInfo<>(blogsCommentPageInfo.getPageNum(), blogsCommentPageInfo.getPageSize(), blogsCommentPageInfo.getTotal(), blogsCommentList);
    }

    private BlogsCommentListReply buildBlogsCommentListReply(BlogsComment blogsComment) {
        User toUser = null;
        if(!StringUtils.isNullOrBlank(blogsComment.getToUser())){
            toUser = userService.findById(blogsComment.getToUser());
        }
        BlogsCommentListReply commentListReply = new BlogsCommentListReply(blogsComment, userService.findById(blogsComment.getFromUser()), toUser);
        if (!StringUtils.isNullOrBlank(blogsComment.getBackofficeUserId())) {
            commentListReply.setOperatorName(Optional.ofNullable(backOfficeUserService.findById(blogsComment.getBackofficeUserId())).orElse(BackOfficeUser.newEmptyBackOfficeUser()).getNickname());
        }
        return commentListReply;
    }

    //人工审核评论
    public GlobalResponseCode manualApprovalComment(ApprovalRequest request) {
        var backOfficeUser = (BackOfficeUser) SecurityUtils.getSubject().getPrincipal();
        var commentStatus = Constant.CommentStatus.forNumber(request.getStatus());
        if (commentStatus == null) return GlobalResponseCode.PARAM_ERROR;
        var ids = request.getIds();
        var blogsComments = blogsCommentService.findByIds(ids);
        if (blogsComments.size() != ids.size()) return GlobalResponseCode.COMMENT_NOT_EXIT;
        for (BlogsComment blogsComment : blogsComments) {
            blogsComment.manualAudit(commentStatus, backOfficeUser.getId());
            if (Objects.equals(commentStatus, Constant.CommentStatus.COMMENT_NO_PASS)) {
                blogsComment.setRefuseReason(request.getRefuseReason());
            }
        }
        blogsCommentService.saveAll(blogsComments);
        //TODO 通知BlogServer 动态审核结果并更新缓存。
        return GlobalResponseCode.SUCCESS;
    }

    //===============动态标签管理===================
    @Autowired
    BlogsLabelsConfigService blogsLabelsConfigService;
    @Autowired
    LabelJsonConfig labelJsonConfig;

    //TODO 初始化标签到数据库
    public void initBlogsLabelConfig() {
        List<BlogsLabelsConfig> labels = blogsLabelsConfigService.findAll();
        if (!CollectionUtils.isEmpty(labels)) return;
        JSONArray allLabels = labelJsonConfig.getLabelsArray();
        List<BlogsLabelsConfig> blogsLabelsConfigs = JSONArray.parseArray(allLabels.toJSONString(), BlogsLabelsConfig.class);
        blogsLabelsConfigs.forEach(label->label.setCreateTime(System.currentTimeMillis()));
        blogsLabelsConfigService.saveAll(blogsLabelsConfigs);
    }

    public Map<String,Object> findBlogsLabels(Long blogsId) {
        List<BlogsLabelsConfig> allLabels = blogsLabelsConfigService.findAll();
        Blogs byId = blogsService.findById(blogsId);
        Set<Integer> labels = byId.getLabels();
        List<BlogsLabelsConfig> have = allLabels.stream().filter(v -> labels.contains(v.getId())).collect(Collectors.toList());
        List<BlogsLabelsConfig> noHave = allLabels.stream().filter(v -> !labels.contains(v.getId())).collect(Collectors.toList());
        Map<String,Object> result = new HashMap<>(2);
        result.put("have",have);
        result.put("noHave",noHave);
        return result;
    }

    public RestResponse updateBlogsLabels(Long blogsId, Set<Integer> labels) {
        Blogs byId = blogsService.findById(blogsId);
        if(!byId.getBlogsStatus().equals(Constant.BlogsStatus.BLOGS_UNAPPROVED)){
            return new ErrorResponse(GlobalResponseCode.PARAM_ERROR.getCode(),"视频状态不支持修改标签").withData(byId);
        }
        List<BlogsLabelsConfig> all = blogsLabelsConfigService.findAll();
        boolean isExit = all.stream().map(BlogsLabelsConfig::getId).anyMatch(labels::contains);
        if(!isExit){
            return new ErrorResponse(GlobalResponseCode.PARAM_ERROR.getCode(),"标签不存在").withData(byId);
        }
        byId.setLabels(labels);
        return new SuccessResponse().withData(blogsService.save(byId));
    }

    public RestResponse delBlogs(Long blogsId) {
        Blogs byId = blogsService.findById(blogsId);
        if(Objects.isNull(byId)){
            return new ErrorResponse(GlobalResponseCode.BLOGS_NOT_EXIST);
        }
        byId.setBlogsStatus(Constant.BlogsStatus.BLOG_DELETE);
        return new SuccessResponse().withData(blogsService.save(byId));
    }
}
