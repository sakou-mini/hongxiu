package com.donglai.web.web.dto.request;

import com.donglai.common.annotation.AutoIncKey;
import lombok.Data;

import javax.persistence.Id;

/**
 * @author Moon
 * @date 2022-01-27 16:12
 */
@Data
public class KeywordInsertRequest {

    /**
     * 词汇
     */
    private String word;
}
