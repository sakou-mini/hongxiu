package com.donglai.web.web.dto.request;

import lombok.Data;

/**
 * @author Moon
 * @date 2021-12-28 14:11
 */
@Data
public class KeywordFindListRequest {

    private String keyword;

    private Boolean status;

    private Long createdTimeStart;

    private Long createdTimeEnd;

    private Integer page;

    private Integer size;
}
