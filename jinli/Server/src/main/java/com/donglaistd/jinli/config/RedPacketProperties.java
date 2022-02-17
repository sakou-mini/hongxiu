package com.donglaistd.jinli.config;

import com.donglaistd.jinli.exception.RedPacketConfigException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource(value = {"classpath:redpacket_config.properties"})
public class RedPacketProperties {
    private int minCoin;
    private int maxCoin;
    private int minNum;
    private int maxNum;
    @Value("${redpacket.count.down.time}")
    private int countDownTime;
    @Value("${redpacket.next.over.time}")
    private int overTime;
    @Value("${redpacket.min.coin.range}")
    private int minRange;
    @Value("${redpacket.enable}")
    private boolean enable;

    public int getMinCoin() {
        return minCoin;
    }

    public int getMaxCoin() {
        return maxCoin;
    }

    public int getMinNum() {
        return minNum;
    }

    public int getMaxNum() {
        return maxNum;
    }

    public int getCountDownTime() {
        return countDownTime;
    }

    public long getOverTime(){
        return overTime;
    }

    public int getMinRange() {
        return minRange;
    }

    public boolean isEnable() {
        return enable;
    }

    public RedPacketProperties(@Value("${redpacket.min.coin}") int minCoin,
                               @Value("${redpacket.min.num}") int minNum,
                               @Value("${redpacket.max.coin}") int maxCoin,
                               @Value("${redpacket.max.num}") int maxNum) {
        if(minCoin / maxNum < 1) throw new RedPacketConfigException("The redPacketConfig  parameter value of the red envelope is wrong, the average minimum amountCoin required is 1");
        this.minCoin = minCoin;
        this.minNum = minNum;
        this.maxCoin = maxCoin;
        this.maxNum = maxNum;
    }
}
