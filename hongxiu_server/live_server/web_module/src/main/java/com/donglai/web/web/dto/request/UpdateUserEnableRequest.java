package com.donglai.web.web.dto.request;

import lombok.Data;

import java.util.List;

/**
 * @author Moon
 * @date 2021-12-29 13:42
 */
@Data
public class UpdateUserEnableRequest {
    private List<String> ids;

    private Boolean enabled;
}
