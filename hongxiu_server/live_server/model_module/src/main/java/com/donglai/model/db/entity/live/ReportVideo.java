package com.donglai.model.db.entity.live;

import com.donglai.common.annotation.AutoIncKey;
import lombok.Data;

import javax.persistence.Id;

/**
 * @author Moon
 * @date 2021-12-27 14:04
 */
@Data
public class ReportVideo {


    @Id
    @AutoIncKey
    private Long id;

    /**
     * 视频ID
     */
    private Long blogId;

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


    public static ReportVideo newInstance(long blogsId, String reason) {
        ReportVideo reportVideo = new ReportVideo();
        reportVideo.setCreatedTime(System.currentTimeMillis());
        reportVideo.setReason(reason);
        reportVideo.setBlogId(blogsId);
        return reportVideo;
    }
}
