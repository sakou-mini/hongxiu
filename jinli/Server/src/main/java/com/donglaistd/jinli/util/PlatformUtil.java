package com.donglaistd.jinli.util;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
@Component
public class PlatformUtil {

    @Autowired
    VerifyUtil verifyUtil;

    public static List<Constant.PlatformType> getAllPlatform(){
       return Arrays.stream(Constant.PlatformType.values())
                .filter(platform -> !Objects.equals(Constant.PlatformType.UNRECOGNIZED, platform) && !Objects.equals(Constant.PlatformType.PLATFORM_DEFAULT, platform))
                .collect(Collectors.toList());
    }

    public int getUserPlatformCreateTag(User user){
        if(user == null) return Constant.PlatformType.PLATFORM_JINLI_VALUE;
        if(verifyUtil.checkIsLiveUser(user)) return  Constant.PlatformType.PLATFORM_JINLI_VALUE;
        if(user.getAccountName().startsWith("platformQ~")) return Constant.PlatformType.PLATFORM_Q_VALUE;
        else if(user.getAccountName().startsWith("platformAccount~")) return Constant.PlatformType.PLATFORM_T_VALUE;
        return Constant.PlatformType.PLATFORM_JINLI_VALUE;
    }
}
