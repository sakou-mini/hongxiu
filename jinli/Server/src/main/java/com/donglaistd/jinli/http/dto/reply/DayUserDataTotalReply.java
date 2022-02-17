package com.donglaistd.jinli.http.dto.reply;

import com.donglaistd.jinli.database.entity.statistic.DayUserDataTotal;
import com.donglaistd.jinli.http.entity.PageInfo;

import java.util.HashSet;
import java.util.Set;

public class DayUserDataTotalReply {
    public PageInfo<DayUserDataTotal> pageInfo;

    public DayUserDataTotal totalData;

    public DayUserDataTotalReply(PageInfo<DayUserDataTotal> pageInfo) {
        this.pageInfo = pageInfo;
        if(pageInfo!=null){
            Set<String> ids = new HashSet<>();
            long sumGiftGiftFlow = 0;
            long sumGiftGiftCount = 0;
            long sumWatchLiveTime = 0;
            long sumBanUserNum = 0; //maybe need remove duplicates
            long sumConnectedLiveCount = 0;
            long sumBulletMessageCount = 0;
            long sumLiveVisitorCount = 0;
            long sumExchangeCoinAmount = 0;
            for (DayUserDataTotal dayUserDataTotal : pageInfo.getContent()) {
                ids.addAll(dayUserDataTotal.getLiveVisitorIds());
                sumGiftGiftFlow += dayUserDataTotal.getGiftFlow();
                sumGiftGiftCount += dayUserDataTotal.getGiftCount();
                sumWatchLiveTime += dayUserDataTotal.getLiveWatchTime();
                sumBanUserNum += dayUserDataTotal.getBanUserNum();
                sumConnectedLiveCount += dayUserDataTotal.getConnectedLiveCount();
                sumBulletMessageCount += dayUserDataTotal.getBulletMessageCount();
                sumLiveVisitorCount += dayUserDataTotal.getLiveVisitorCount();
                sumExchangeCoinAmount += dayUserDataTotal.getExchangeCoinAmount();
            }
            DayUserDataTotal totalData = new DayUserDataTotal();
            totalData.setLiveVisitorIds(ids);
            totalData.setGiftFlow(sumGiftGiftFlow);
            totalData.setGiftCount(sumGiftGiftCount);
            totalData.setLiveWatchTime(sumWatchLiveTime);
            totalData.setBanUserNum(sumBanUserNum);
            totalData.setConnectedLiveCount(sumConnectedLiveCount);
            totalData.setBulletMessageCount(sumBulletMessageCount);
            totalData.setLiveVisitorCount(sumLiveVisitorCount);
            totalData.setExchangeCoinAmount(sumExchangeCoinAmount);
            this.totalData = totalData;
        }
    }

    public PageInfo<DayUserDataTotal> getPageInfo() {
        return pageInfo;
    }

    public void setPageInfo(PageInfo<DayUserDataTotal> pageInfo) {
        this.pageInfo = pageInfo;
    }

    public DayUserDataTotal getTotalData() {
        return totalData;
    }
}
