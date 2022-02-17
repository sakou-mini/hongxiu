package com.donglaistd.jinli.constant;

public enum QueueType
{
    NONE(0),SEND_ROLL_MESSAGE(1),DAY_ONE_OCLOCK(2),LIVE_LIMIT_AUTO_CLOSE(3);

    private final int value;
    QueueType(int i)
    {
        this.value = i;
    }

    public static QueueType valueOf(int value)
    {
        switch (value) {
            case 1: return SEND_ROLL_MESSAGE;
            case 2: return DAY_ONE_OCLOCK;
            case 3: return LIVE_LIMIT_AUTO_CLOSE;
            default:return NONE;
        }
    }

    public int getValue()
    {
        return this.value;
    }

}
