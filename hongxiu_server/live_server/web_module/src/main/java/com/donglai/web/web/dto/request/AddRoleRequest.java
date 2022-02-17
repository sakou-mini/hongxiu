package com.donglai.web.web.dto.request;

/**
 * @author Moon
 * @date 2021-12-29 14:52
 */

import lombok.Data;

import java.util.List;

@Data
public class AddRoleRequest {

    private String roleName;

    private Boolean status;

    private List<String> menuIds;
}
