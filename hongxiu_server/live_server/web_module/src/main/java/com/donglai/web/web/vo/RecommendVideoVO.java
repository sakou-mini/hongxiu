package com.donglai.web.web.vo;

import lombok.Data;

import java.util.List;

/**
 * @author Moon
 * @date 2022-01-05 15:29
 */
@Data
public class RecommendVideoVO {
    private Long id;

    private List<String> url;

    private String content;

    private String blogCreateName;

    private String blogUserId;

    private Long createdTime;

    private Long updatedTime;

    private String createdId;
}
