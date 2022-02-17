package com.donglai.model.db.entity.live;

import com.donglai.common.annotation.AutoIncKey;
import lombok.Data;

import javax.persistence.Id;

/**
 * @author Moon
 * @date 2021-12-27 15:02
 */
@Data
public class ReportComment {


    @Id
    @AutoIncKey
    private Long id;

    /**
     * 视频ID
     */
    private Long commentId;

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

    public static ReportComment newInstance(String reason, Long commentId) {
        ReportComment reportComment = new ReportComment();
        reportComment.setCommentId(commentId);
        reportComment.setReason(reason);
        reportComment.setCreatedTime(System.currentTimeMillis());
        return reportComment;
    }
}
