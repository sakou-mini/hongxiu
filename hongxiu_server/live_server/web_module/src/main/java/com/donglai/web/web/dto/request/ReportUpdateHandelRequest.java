package com.donglai.web.web.dto.request;

import lombok.Data;

import java.util.List;

/**
 * @author Moon
 * @date 2021-12-27 17:04
 */
@Data
public class ReportUpdateHandelRequest {

    private List<Long> ids;

    private Boolean handle;
}
