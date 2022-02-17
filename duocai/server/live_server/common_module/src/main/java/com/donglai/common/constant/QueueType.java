package com.donglai.common.constant;

/*要执行的定时队列类型*/
public enum QueueType
{
    NONE(0),END_LIVE(1),TIMEOUT_END_LIVE(2),GIFT_CONTRIBUTE_INCOME_RANK(3),REVIEW_BOLGS(4),
    ACCOUNT_DAILY_STATISTIC(6), //每日统计
    LIVE_MINUTE_JOB(7); //直播服每分钟任务

    private final int value;
    QueueType(int i)
    {
        this.value = i;
    }

    public int getValue()
    {
        return this.value;
    }

    public static QueueType valueOf(int value)
    {
        switch (value) {
            case 1: return END_LIVE;
            case 2: return TIMEOUT_END_LIVE;
            case 3: return GIFT_CONTRIBUTE_INCOME_RANK;
            case 4: return REVIEW_BOLGS;
            case 6: return ACCOUNT_DAILY_STATISTIC;
            case 7: return LIVE_MINUTE_JOB;
            default:return NONE;
        }
    }

}
