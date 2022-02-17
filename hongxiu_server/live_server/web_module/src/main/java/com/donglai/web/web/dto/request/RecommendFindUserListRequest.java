package com.donglai.web.web.dto.request;

import lombok.Data;

/**
 * @author Moon
 * @date 2022-01-06 10:16
 */
@Data
public class RecommendFindUserListRequest {

    private String userId;

    private String status;

    private Long createdTimeStart;

    private Long createdTimeEnd;

    private Integer page;

    private Integer size;
}
