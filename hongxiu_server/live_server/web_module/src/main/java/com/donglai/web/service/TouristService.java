package com.donglai.web.service;

import com.donglai.model.db.entity.statistics.DailyOfServerData;
import com.donglai.web.response.PageInfo;
import com.donglai.web.web.dto.request.TouristListRequest;
import com.donglai.web.web.vo.TouristVO;

/**
 * @author Moon
 * @date 2021-12-22 10:39
 */
public interface TouristService {

    PageInfo<TouristVO> touristList(TouristListRequest request);

    PageInfo<DailyOfServerData> touristStatisticsList(TouristListRequest request);
}
