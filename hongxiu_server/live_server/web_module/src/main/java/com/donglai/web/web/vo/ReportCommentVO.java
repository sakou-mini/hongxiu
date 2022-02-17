package com.donglai.web.web.vo;

import lombok.Data;

/**
 * @author Moon
 * @date 2021-12-27 15:32
 */
@Data
public class ReportCommentVO {
    private Long id;

    private Long commentId;

    /**
     * 评论内容
     */
    private String comment;
    /**
     * 发布者姓名
     */
    private String commentAuthorName;
    /**
     * 视频作者Id
     */
    private String commentAuthorId;
    /**
     * 是否已处理
     */
    private Boolean handle;
    /**
     * 举报原因
     */
    private String reason;
    /**
     * 创建人名字
     */
    private String createdId;
    /**
     * 举报时间
     */
    private Long createdTime;
    /**
     * 举报人IP
     */
    private String createdIp;

}
