package com.donglai.web.web.dto.request;

import lombok.Data;

import java.util.List;

/**
 * @author Moon
 * @date 2021-12-28 18:02
 */
@Data
public class FeedbackDeleteRequest {
    private List<Long> ids;
}
