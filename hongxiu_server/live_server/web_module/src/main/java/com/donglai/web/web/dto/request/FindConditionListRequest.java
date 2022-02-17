package com.donglai.web.web.dto.request;

import lombok.Data;

/**
 * @author Moon
 * @date 2021-12-29 14:17
 */
@Data
public class FindConditionListRequest {

    private Integer status;

    private Long createdTimeStart;

    private Long createdTimeEnd;

    private Integer page;

    private Integer size;
}
