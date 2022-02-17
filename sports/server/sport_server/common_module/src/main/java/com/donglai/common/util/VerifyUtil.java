package com.donglai.common.util;

import com.donglai.protocol.Constant;

import java.util.Objects;

import static com.donglai.protocol.Constant.LiveUserStatus.LIVE_LIVE;
import static com.donglai.protocol.Constant.LiveUserStatus.LIVE_OFFLINE;

public class VerifyUtil {

    public static boolean isPassLiveUserStatus(Constant.LiveUserStatus statue){
        return Objects.equals(LIVE_OFFLINE, statue) || Objects.equals(LIVE_LIVE, statue);
    }
}
