package com.donglai.web.web.dto.request;

import lombok.Data;

/**
 * @author Moon
 * @date 2021-12-31 11:18
 */
@Data
public class BackOfficeLogFindListRequest {
    private Boolean res;

    private Long addTimeStart;

    private Long addTimeEnd;

    private Integer page;

    private Integer size;
}
