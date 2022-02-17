package com.donglai.web.web.vo;

import lombok.Data;

/**
 * @author Moon
 * @date 2021-12-28 14:25
 */
@Data
public class KeywordVO {

    private Long id;

    /**
     * 词汇
     */
    private String word;
    /**
     * 状态
     */
    private Boolean status;
    /**
     * 创建时间
     */
    private Long createdTime;
    /**
     * 更新时间
     */
    private Long updatedTime;
    /**
     * 操作人
     */
    private String createdId;
}
