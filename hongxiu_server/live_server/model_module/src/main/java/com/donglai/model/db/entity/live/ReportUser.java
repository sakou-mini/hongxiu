package com.donglai.model.db.entity.live;

import com.donglai.common.annotation.AutoIncKey;
import lombok.Data;

import javax.persistence.Id;

/**
 * @author Moon
 * @date 2021-12-27 16:53
 */
@Data
public class ReportUser {


    @Id
    @AutoIncKey
    private Long id;

    /**
     * 被举报用户ID
     */
    private String userId;

    /**
     * 是否已处理
     */
    private boolean handle;

    /**
     * 举报原因
     */
    private String reason;

    /**
     * 举报人
     */
    private String createdId;


    /**
     * 举报时间
     */
    private Long createdTime;

    /**
     * 创建者Ip
     */
    private String createdIp;

    public static ReportUser newInstance(String userId, String reason) {
        ReportUser reportUser = new ReportUser();
        reportUser.setReason(reason);
        reportUser.setUserId(userId);
        reportUser.setCreatedTime(System.currentTimeMillis());
        return reportUser;
    }
}
