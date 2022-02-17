package com.donglai.model.db.entity.back;

import com.donglai.common.annotation.AutoIncKey;
import lombok.Data;

import javax.persistence.Id;

/**
 * @author Moon
 * @date 2022-01-05 11:28
 */
@Data
public class RecommendUser {


    @Id
    @AutoIncKey
    private Long id;

    private String userId;

    private String txt;

    private Long joinTime;

    private Long createdTime;

    private Long updatedTime;

    private String createId;

}
