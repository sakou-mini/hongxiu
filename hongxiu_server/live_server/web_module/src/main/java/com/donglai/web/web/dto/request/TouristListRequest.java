package com.donglai.web.web.dto.request;

import lombok.Data;

/**
 * @author Moon
 * @date 2021-12-22 10:32
 */
@Data
public class TouristListRequest {
    /**
     * 游客ID
     */
    private String touristId;

    /**
     * 最近登陆IP
     */
    private String lastIp;

    /**
     * 初次登录IP
     */
    private String firstIp;

    /**
     * 最近登陆范围开始
     */
    private Long lastLoginTimeStart;

    /**
     * 最近登陆时间范围结束
     */
    private Long lastLoginTimeEnd;

    /**
     * 初次登陆范围开始
     */
    private Long firstLoginTimeStart;

    /**
     * 初次登陆范围结束
     */
    private Long firstLoginTimeEnd;
    /**
     * 统计开始日期
     */
    private Long touristStatisticsTimeStart;
    /**
     * 统计结束日期
     */
    private Long touristStatisticsTimeEnd;
    /**
     * 页码
     */
    private Integer page;
    /**
     * 条数
     */
    private Integer size;

}
