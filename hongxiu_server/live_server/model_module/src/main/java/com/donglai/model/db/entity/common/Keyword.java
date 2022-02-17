package com.donglai.model.db.entity.common;

import com.donglai.common.annotation.AutoIncKey;
import lombok.Data;

import javax.persistence.Id;

/**
 * @author Moon
 * @date 2021-12-28 13:43
 */
@Data
public class Keyword {

    @Id
    @AutoIncKey
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
