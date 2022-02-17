package com.donglai.web.web.vo;

import lombok.Data;

/**
 * @author Moon
 * @date 2021-12-28 16:44
 */
@Data
public class FeedBackVO {

    private Long id;

    private String content;

    private Integer type;

    private String createdId;

    private String phone;

    private Long createdTime;

    private String createdIp;

    private Integer status;

    private String reply;
}
