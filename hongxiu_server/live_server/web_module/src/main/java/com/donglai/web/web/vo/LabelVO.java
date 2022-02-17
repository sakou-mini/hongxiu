package com.donglai.web.web.vo;

import lombok.Data;

/**
 * @author Moon
 * @date 2021-12-24 11:20
 */
@Data
public class LabelVO {
    private Integer id;

    private String labelName;

    private Integer useCount;

    private Boolean enable;

    private Long createTime;

    private Long updateTime;

    private String backstageUserId;

    private Integer type;
}
