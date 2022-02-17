package com.donglai.web.web.dto.request;

import lombok.Data;

/**
 * @author Moon
 * @date 2021-12-22 10:19
 */
@Data
public class ResetUserRequest {

    private String userId;

    /**
     * 0 用户名
     * 1 头像
     * 2 简介
     */
    private Integer resetType;

}
