package com.donglai.web.web.vo;

import lombok.Data;

/**
 * @author Moon
 * @date 2021-12-22 10:48
 */
@Data
public class TouristVO {
    private String id;

    private String accountId;

    private Long likeCount;

    private Long loginCount;

    private Long lastLoginTime;

    private String lastLoginIp;

    private Long firstLoginTime;

    private String firstLoginIp;
}
