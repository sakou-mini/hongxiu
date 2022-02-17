package com.donglai.web.service;

import com.donglai.web.response.PageInfo;
import com.donglai.web.response.RestResponse;
import com.donglai.web.web.dto.request.FeedbackDeleteRequest;
import com.donglai.web.web.dto.request.FeedbackFindListRequest;
import com.donglai.web.web.dto.request.FeedbackUpdateStatusRequest;
import com.donglai.web.web.vo.FeedBackVO;

/**
 * @author Moon
 * @date 2021-12-28 16:21
 */
public interface FeedBackBackService {
    PageInfo<FeedBackVO> findList(FeedbackFindListRequest request);

    RestResponse check(FeedbackUpdateStatusRequest request);

    RestResponse reply(FeedbackUpdateStatusRequest request);

    RestResponse delete(FeedbackDeleteRequest request);
}
