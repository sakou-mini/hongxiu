package com.donglai.web.web.dto.request;

import lombok.Data;

/**
 * @author Moon
 * @date 2021-12-29 10:36
 */
@Data
public class BackOfficeUserFindListRequest {
    private String roleGroup;

    private Boolean enabled;

    private Long createdTimeStart;

    private Long createdTimeEnd;

    private Integer page;

    private Integer size;

}
