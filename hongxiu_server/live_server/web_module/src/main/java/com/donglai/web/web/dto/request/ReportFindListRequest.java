package com.donglai.web.web.dto.request;

import lombok.Data;

/**
 * @author Moon
 * @date 2021-12-27 13:49
 */
@Data
public class ReportFindListRequest {
    /**
     * 状态
     */
    private Boolean handel;
    /**
     * 举报时间开始
     */
    private Long addStartTime;
    /**
     * 举报时间结束
     */
    private Long addEndTime;
    /**
     * 页码
     */
    private Integer page;
    /**
     * 条数
     */
    private Integer size;
}
