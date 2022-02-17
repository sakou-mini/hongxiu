package com.donglai.common.constant;

/*要执行的定时队列类型*/
public enum QueueType {
    NONE(0),
    END_LIVE(1),//直播结束倒计时
    TIMEOUT_END_LIVE(2),//超时自动关闭
    GIFT_CONTRIBUTE_INCOME_RANK(3),//礼物排行榜
    REVIEW_BOLGS(4),//审核动态
    BLOGS_UPLOAD_LIKE(5),//定时更新动态点赞数据
    DAILY_STATISTIC(6), //统计服每日统计
    MINUTE_STATISTIC(7); //分钟统计

    private final int value;

    QueueType(int i) {
        this.value = i;
    }

    public int getValue() {
        return this.value;
    }

    public static QueueType valueOf(int value) {
        switch (value) {
            case 1:
                return END_LIVE;
            case 2:
                return TIMEOUT_END_LIVE;
            case 3:
                return GIFT_CONTRIBUTE_INCOME_RANK;
            case 4:
                return REVIEW_BOLGS;
            case 5:
                return BLOGS_UPLOAD_LIKE;
            case 6:
                return DAILY_STATISTIC;
            case 7:
                return MINUTE_STATISTIC;
            default:
                return NONE;
        }
    }

}
