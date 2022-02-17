package com.donglai.web.web.dto.request;

import lombok.Data;

/**
 * @author Moon
 * @date 2022-01-04 16:15
 */
@Data
public class BannerFindListRequest {
    private String title;


    private Integer status;


    private Long startTime;


    private Long endTime;


    private Long addStartTime;


    private Long addEndTime;

    private Integer page;

    private Integer size;
}
