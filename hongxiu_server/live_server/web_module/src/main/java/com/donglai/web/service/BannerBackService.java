package com.donglai.web.service;

import com.donglai.model.db.entity.live.Banner;
import com.donglai.web.response.PageInfo;
import com.donglai.web.response.RestResponse;
import com.donglai.web.web.dto.request.BannerFindListRequest;
import com.donglai.web.web.dto.request.BannerUpdateStatusRequest;

/**
 * @author Moon
 * @date 2022-01-04 16:22
 */
public interface BannerBackService {
    PageInfo<Banner> findList(BannerFindListRequest request);

    RestResponse updateStatus(BannerUpdateStatusRequest request);

    RestResponse delete(BannerUpdateStatusRequest request);

    RestResponse insert(Banner request);

    RestResponse update(Banner request);
}
