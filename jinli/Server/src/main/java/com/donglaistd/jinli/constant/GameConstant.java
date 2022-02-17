package com.donglaistd.jinli.constant;


import com.donglaistd.jinli.Constant;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.donglaistd.jinli.Constant.CardNumber.*;
import static com.donglaistd.jinli.Constant.PlatformType.PLATFORM_T;
import static com.donglaistd.jinli.database.entity.backoffice.BackOfficeRole.PLATFORM_Q;

public class GameConstant {
    public static final int BULL_BULL_CARD_SIZE = 5;
    public static final int GOLDEN_FLOWER_CARD_SIZE = 3;

    public static final int SMALLBULL_POINT = 10;
    public static final int SMALLBULL_CARD_MAX_POINT = 5;
    public static final int BOOM_CARDSIZE = 4;
    public static final int FIGUREBULL_4_FACECARD_SIZE = 4;
    public static final int FIGUREBULL_5_FACECARD_SIZE = 5;

    public static final int MAX_CONTACT_RECORD_NUM = 50;
    public static final int MESSAGE_OVERDAY = 7;

    public static final int LANDLORDS_POKER_BASE_SIZE = 17;
    public static final int LANDLORD_BASE_CARD_SIZE = 3;
    public static final int LANDLORD_MAX_GRAB_ROUND = 2;
    public static final int LANDLORD_STRAIGHT_MIN_SIZE = 5;
    public static final int POKER_THREE_ATTACH_TWO_SIZE = 5;
    public static final int POKER_THREE_ATTACH_ONE_SIZE = 4;
    public static final int POKER_FOUR_ATTACH_TWO_MIN_SIZE = 6;
    public static final long POKER_PAIR_CARD_SIZE = 2;
    public static final int POKER_SERIAL_PAIR_MIN_SIZE = 6;
    public static final int POKER_PLANE_MIN_SIZE = 2;
    public static final int POKER_SINGLE_SIZE = 1;

    public static final int RESTFUL_RESULT_FAILED = -1;
    public static final int RESTFUL_RESULT_SUCCESS = 0;
    public static final int RESTFUL_RESULT_MISSING_PARAMETER = 1;
    public static final int RESTFUL_RESULT_PLANT_ACCOUNT_NOT_EXIST = 2;
    public static final int RESTFUL_RESULT_RECEIVER_NOT_EXIST = 3;
    public static final int RESTFUL_RESULT_TOKEN_EXPIRE = 3;
    public static final int RESTFUL_NOT_INIT_PLANT_LIVE = 4;
    public static final int RESTFUL_RESULT_DISPLAY_EXIST = 10;

    public static final int FIRST_RANK = 1;
    public static final int SECOND_RANK = 2;
    public static final int THIRD_RANK = 3;
    public static final int POKER_THREE_SIZE = 3;
    public static final int POKER_MAX_RATE = 9999;
    public static final int PREDICT_DEFAULT_TIME = 60;
    public static final int ONLY_ONE_PLAYER = 1;
    public static final int ONLY_TWO_PLAYER = 2;
    public static final int ONLY_THREE_PLAYER = 3;
    public static final int GENERAL_NUMBER = 1;
    public static final List<Constant.CardNumber> Min_Straight_GoldenFlower = Lists.newArrayList(Ace, Two, Three);
    public static final List<Constant.CardNumber> Min_Straight_Texas = Lists.newArrayList(Ace, Two, Three, Four, Five);
    public static final int TEXAS_CARD_SIZE = 5;
    public static final int TEXAS_FOUR_OF_KIND_SAME_SIZE = 4;
    public static final int TEXAS_THREE_OF_KIND_SAME_SIZE = 3;
    public static final int TEXAS_PAIR_SAME_SIZE = 2;

    public static final String DEFAULT_AVATAR_PATH = "/images/avatar/default/img_avatar.png";
    public static final String DEFAULT_PLATFORM_T_AVATAR_PATH = "/images/avatar/default/platform_t_avatar.png";
    public static final String DEFAULT_ROOM_IMAGE_PATH = "/images/live/img_live_room.png";

    public static final int ROOM_CHAT_MAX_NUM = 50;

    public static final int LIVE_USER_ID_LENGTH = 6;
    public static final int ROOM_ID_LENGTH = 5;
    public static final String SERVER_ID = "1";
    public static final long TOURIST_EXPIRED_TIME = 1800000;
    public static final String HTTP_URL = "http://";
    public static final String ORIENTATION_VERTICAL = "vertical";
    public static final String ORIENTATION_HORIZONTAL = "horizontal";

    public static final String PLATFORMUSER_SPLITTER = "~";
    public static final String PLATFOR_NAME_Q_SPLITTER = "platformQ_";

    public static final String BROADCAST_TYPE_LIVE_PLAY_MUSIC = "LivePlayMusic";
    public static final String BROADCAST_TYPE_LIVE_PAUSE_MUSIC = "LivePauseMusic";
    public static final String BROADCAST_TYPE_LIVE_STOP_MUSIC = "LiveStopMusic";

    public static final int WEEK_DAYS = 7;
    public static final int MONTH_DAYS = 30;

    //BackOffice HttpResultCode
    public static final int HTTP_RESULT_SUCCESS = 200;
    public static final int HTTP_RESULT_USER_IS_LIVEUSER = 1000;
    public static final int AUDIENCE_SIZE = 5;
    public static final int MAX_MUTE_CHAT_RECORD = 20;
    public static final int EMPTY_GAME_CLOSE_DELAY_TIME = 10000;

    public static final int DOMAIN_EXIT = 201;
    public static final int DOMAIN_PARAM_ERROR = 202;
    public static final int DOMAIN_NUM_OVERLIMIT = 203;

    public static final int LOGIN_FAILED = 400;
    public static final int ACCOUNT_DISABLED_ERROR = 505;

    public static final int LIVE_USER_NOT_EXIT = 510;
    public static final int PARAM_ERROR = 511;
    public static final int UPLOAD_FILE_ERROR = 513;

    public static final int SUCCESS = 200;

    //domain prefix
    public static final String API = "api";
    public static final String H5 = "h5";
    public static final String EGW = "egw";
    public static final String WS = "ws";
    public static final String CONNECTED_PATH = "/share/giftJson?platform="+ Constant.PlatformType.PLATFORM_JINLI.name();
    public static final int MAX_DOMAIN_NUM = 6;

    public static final int DAY_OF_MAX_HOUR = 23;
    public static final int DAY_OF_MIN_HOUR = 0;

    //T平台官方直播间模拟主播名
    public static final String PLATFORM_T_OFFICIAL_LIVE_NAME = PLATFORM_T.name() + "~officialLive";
    public static final String PLATFORM_T_OFFICIAL_ROOM_IMAGE = "/images/live/room";
    public static final List<String> PLATFORM_T_OFFICIAL_DISPLAYNAME_LIST = Lists.newArrayList("(甜甜)"
            , "(蛋蛋)", "(小妮娜)", "(瑪妮)");
    public static final String PLATFORM_T_OFFICIAL_AVATAR_IMAGE = "/images/avatar/default/tuser";

    //Q平台官方直播间模拟主播名
    public static final String PLATFORM_Q_OFFICIAL_LIVE_NAME = PLATFORM_Q.name() + "~officialLive";
    public static final String PLATFORM_Q_OFFICIAL_ROOM_IMAGE = "/images/live/roomQ";
    public static final List<String> PLATFORM_Q_OFFICIAL_DISPLAYNAME_LIST = Lists.newArrayList("(梦露)", "(瑪妮)","(艾米)", "(小果)");
    public static final String PLATFORM_Q_OFFICIAL_AVATAR_IMAGE = "/images/avatar/default/quser";


    public static List<String> OFFICIAL_VIDEOS = new ArrayList<>();

    public  static List<String> getOfficialVideos(){
        if(!OFFICIAL_VIDEOS.isEmpty()) {
            Collections.shuffle(OFFICIAL_VIDEOS);
            return OFFICIAL_VIDEOS;
        }
        if(OFFICIAL_VIDEOS.isEmpty())
        for (int i = 1; i < 35; i++) {
            OFFICIAL_VIDEOS.add("/images/live/audio/" + i + ".mp3");
        }
        return OFFICIAL_VIDEOS;
    }
}
