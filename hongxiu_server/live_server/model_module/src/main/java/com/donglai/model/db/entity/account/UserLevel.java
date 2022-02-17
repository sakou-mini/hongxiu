package com.donglai.model.db.entity.account;

import com.donglai.common.annotation.AutoIncKey;
import lombok.Data;

import javax.persistence.Id;

/**
 * @author Moon
 * @date 2022-02-14 11:00
 */
@Data
public class UserLevel {

    @Id
    @AutoIncKey
    private String id;

    /**
     * 等级名称
     */
    private String levelName;

    /**
     * 等级
     */
    private int level;

    /**
     * 等级所需积分
     */
    private int levelIntegral;

}
