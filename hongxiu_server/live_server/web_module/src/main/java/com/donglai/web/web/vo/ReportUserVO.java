package com.donglai.web.web.vo;

import lombok.Data;

/**
 * @author Moon
 * @date 2021-12-27 16:23
 */
@Data
public class ReportUserVO {
    private Long id;

    private String userId;
    /**
     * 被举报用户名字
     */
    private String nickname;
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
