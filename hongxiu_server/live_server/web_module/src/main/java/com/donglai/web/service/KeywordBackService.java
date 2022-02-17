package com.donglai.web.service;

import com.donglai.web.response.PageInfo;
import com.donglai.web.response.RestResponse;
import com.donglai.web.web.dto.request.KeywordFindListRequest;
import com.donglai.web.web.dto.request.KeywordInsertRequest;
import com.donglai.web.web.dto.request.KeywordUpdateStatusRequest;
import com.donglai.web.web.vo.KeywordVO;

/**
 * @author Moon
 * @date 2021-12-28 14:10
 */
public interface KeywordBackService {
    PageInfo<KeywordVO> findList(KeywordFindListRequest request);

    RestResponse updateStatus(KeywordUpdateStatusRequest request);

    RestResponse delete(KeywordUpdateStatusRequest request);

    RestResponse insert(KeywordInsertRequest request);
}
