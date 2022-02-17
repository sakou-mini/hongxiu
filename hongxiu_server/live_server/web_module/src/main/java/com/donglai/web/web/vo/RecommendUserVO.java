package com.donglai.web.web.vo;

import lombok.Data;

/**
 * @author Moon
 * @date 2022-01-06 10:28
 */
@Data
public class RecommendUserVO {

    private Long id;

    private String nickname;

    private String userId;

    private Long createdTime;

    private Long updatedTime;

    private String txt;

    private String createId;
}
