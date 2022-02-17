package com.donglai.web.web.dto.request;

import lombok.Data;

/**
 * @author Moon
 * @date 2021-12-28 16:15
 */
@Data
public class FeedbackFindListRequest {

    private Integer type;

    private Integer status;

    private Integer page;

    private Integer size;
}
