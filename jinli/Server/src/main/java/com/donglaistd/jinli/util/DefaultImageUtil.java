package com.donglaistd.jinli.util;

import com.donglaistd.jinli.Constant;

import static com.donglaistd.jinli.constant.GameConstant.DEFAULT_AVATAR_PATH;

public class DefaultImageUtil {

    public static String getRaceImageByRaceTypeAndRaceLevel(Constant.RaceType raceType,int level){
        switch (raceType){
            case TEXAS:
                if(level>6 || level < 1)
                    return "/images/race/texas1.png";
                return "/images/race/texas" + level + ".png";
            case LANDLORDS:
                if(level>6 || level < 1)
                    return "/images/race/landlord1.png";
                return "/images/race/landlord" + level + ".png";
            case GOLDEN_FLOWER:
                if(level>6 || level < 1)
                    return "/images/race/goldenflower1.png";
                return "/images/race/goldenflower" + level + ".png";
        }
        return "";
    }

    public static String getUserDefaultByNumber(int number){
        if(number > 10 || number < 0) return DEFAULT_AVATAR_PATH;
        return  "/images/avatar/user/user" + number + ".jpg";
    }

}
