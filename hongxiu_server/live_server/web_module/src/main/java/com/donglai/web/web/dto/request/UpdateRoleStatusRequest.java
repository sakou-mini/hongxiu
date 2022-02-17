package com.donglai.web.web.dto.request;

import lombok.Data;

import java.util.List;

/**
 * @author Moon
 * @date 2021-12-29 15:16
 */
@Data
public class UpdateRoleStatusRequest {
    private List<String> ids;

    private Boolean status;
}
