package com.donglai.web.web.dto.request;

import lombok.Data;

/**
 * @author Moon
 * @date 2022-01-05 13:42
 */
@Data
public class RecommendFindVideoListRequest {

    private String userId;

    private Long createdTimeStart;

    private Long createdTimeEnd;

    private Integer page;

    private Integer size;
}
