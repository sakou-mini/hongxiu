package com.donglai.common.util;

import com.donglai.protocol.Constant;

import java.util.Objects;

import static com.donglai.protocol.Constant.LiveUserStatus.LIVE_LIVE;
import static com.donglai.protocol.Constant.LiveUserStatus.LIVE_OFFLINE;

public class VerifyUtil {

    public static boolean isPassLiveUserStatus(Constant.LiveUserStatus status){
        return Objects.equals(LIVE_OFFLINE, status) || Objects.equals(LIVE_LIVE, status);
    }
}
