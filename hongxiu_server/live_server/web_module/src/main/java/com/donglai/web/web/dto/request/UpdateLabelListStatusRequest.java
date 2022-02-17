package com.donglai.web.web.dto.request;

import lombok.Data;

import java.util.List;

/**
 * @author Moon
 * @date 2021-12-24 13:57
 */
@Data
public class UpdateLabelListStatusRequest {
    private List<Integer> labels;

    private Boolean status;
}
