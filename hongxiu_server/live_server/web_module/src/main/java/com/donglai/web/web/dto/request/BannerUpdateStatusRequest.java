package com.donglai.web.web.dto.request;

import lombok.Data;

import java.util.List;

/**
 * @author Moon
 * @date 2022-01-04 17:16
 */
@Data
public class BannerUpdateStatusRequest {

    private List<Long> ids;

    private Integer status;
}
