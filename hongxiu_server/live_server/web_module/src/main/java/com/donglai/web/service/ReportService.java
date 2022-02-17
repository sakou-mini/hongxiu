package com.donglai.web.service;

import com.donglai.web.response.PageInfo;
import com.donglai.web.response.RestResponse;
import com.donglai.web.web.dto.request.ReportFindListRequest;
import com.donglai.web.web.dto.request.ReportUpdateHandelRequest;
import com.donglai.web.web.vo.ReportCommentVO;
import com.donglai.web.web.vo.ReportUserVO;
import com.donglai.web.web.vo.ReportVideoVO;

/**
 * @author Moon
 * @date 2021-12-27 13:47
 */
public interface ReportService {

    /**
     * 查找视频举报列表
     *
     * @param request 筛选参数
     * @return 成功
     */
    PageInfo<ReportVideoVO> findVideoList(ReportFindListRequest request);

    /**
     * 查找评论举报列表
     *
     * @param request 筛选参数
     * @return 成功
     */
    PageInfo<ReportCommentVO> findCommentList(ReportFindListRequest request);

    /**
     * 查找用户举报列表
     *
     * @param request 筛选参数
     * @return 成功
     */
    PageInfo<ReportUserVO> findUserList(ReportFindListRequest request);

    /**
     * 查看视频举报
     *
     * @param request 传参
     * @return 返回结果
     */
    RestResponse updateVideoHandel(ReportUpdateHandelRequest request);

    /**
     * 查看评论举报
     *
     * @param request 传参
     * @return 返回结果
     */
    RestResponse updateCommentHandel(ReportUpdateHandelRequest request);

    /**
     * 查看用户举报
     *
     * @param request 传参
     * @return 返回结果
     */
    RestResponse updateUserHandel(ReportUpdateHandelRequest request);
}
