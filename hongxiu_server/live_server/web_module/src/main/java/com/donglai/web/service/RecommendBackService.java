package com.donglai.web.service;

import com.donglai.web.response.PageInfo;
import com.donglai.web.response.RestResponse;
import com.donglai.web.web.dto.request.RecommendFindUserListRequest;
import com.donglai.web.web.dto.request.RecommendFindVideoListRequest;
import com.donglai.web.web.dto.request.RecommendUpdateStatusRequest;
import com.donglai.web.web.vo.RecommendUserVO;
import com.donglai.web.web.vo.RecommendVideoVO;

/**
 * @author Moon
 * @date 2022-01-05 13:49
 */
public interface RecommendBackService {
    PageInfo<RecommendVideoVO> findVideoList(RecommendFindVideoListRequest request);

    RestResponse updateVideoStatus(RecommendUpdateStatusRequest request);

    RestResponse deletedVideo(RecommendUpdateStatusRequest request);

    PageInfo<RecommendUserVO> findUserList(RecommendFindUserListRequest request);

    RestResponse updateUserStatus(RecommendUpdateStatusRequest request);

    RestResponse deletedUser(RecommendUpdateStatusRequest request);

    RestResponse addVideo(Long blogId);

    RestResponse addUser(String userId, String log);
}
