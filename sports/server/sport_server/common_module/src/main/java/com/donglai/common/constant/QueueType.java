package com.donglai.common.constant;

/*要执行的定时队列类型*/
public enum QueueType
{
    NONE(0),
    END_LIVE(1), //结束直播
    TIMEOUT_END_LIVE(2),//直播断线超时
    GIFT_CONTRIBUTE_INCOME_RANK(3), //礼物贡献排行
    SPORT_EVENT(5), //体育赛事 更新
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
            case 5: return SPORT_EVENT;
            case 7: return LIVE_MINUTE_JOB;
            default:return NONE;
        }
    }

}
