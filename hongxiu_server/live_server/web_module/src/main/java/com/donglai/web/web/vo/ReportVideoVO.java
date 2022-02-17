package com.donglai.web.web.vo;

import lombok.Data;

import java.util.List;

/**
 * @author Moon
 * @date 2021-12-27 13:59
 */
@Data
public class ReportVideoVO {
    private Long id;

    private Long blogId;
    /**
     * 视频链接
     */
    private List<String> videoUrl;
    /**
     * 视频简介
     */
    private String videoContent;
    /**
     * 视频作者名
     */
    private String videoAuthorName;
    /**
     * 视频作者Id
     */
    private String videoAuthorId;
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
