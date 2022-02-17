package com.donglaistd.jinli.util;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.config.ZoneConfigProperties;
import com.donglaistd.jinli.database.dao.BackOfficeUserRepository;
import com.donglaistd.jinli.database.dao.LiveUserDaoService;
import com.donglaistd.jinli.database.dao.RoomDaoService;
import com.donglaistd.jinli.database.entity.LiveUser;
import com.donglaistd.jinli.database.entity.Room;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.backoffice.BackOfficeUser;
import com.donglaistd.jinli.database.entity.game.CardGame;
import com.donglaistd.jinli.database.entity.zone.PersonDiary;
import com.donglaistd.jinli.http.entity.UploadFileInfo;
import com.donglaistd.jinli.service.LiveLimitProcess;
import com.donglaistd.jinli.util.platform.IPlatformService;
import com.donglaistd.jinli.util.platform.PlatformServiceFactory;
import com.google.common.collect.Lists;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import static com.donglaistd.jinli.Constant.GameType.*;
import static com.donglaistd.jinli.Constant.LiveStatus.*;
import static com.donglaistd.jinli.Constant.ResultCode.*;
import static com.donglaistd.jinli.constant.WebKeyConstant.DIARYID;
import static com.donglaistd.jinli.database.entity.backoffice.BackOfficeRole.*;

@Component
public class VerifyUtil {
    private static final Logger logger = Logger.getLogger(VerifyUtil.class.getName());
    @Autowired
    DataManager dataManager;
    @Autowired
    ZoneConfigProperties zoneConfigProperties;
    @Autowired
    WordFilterUtil wordFilterUtil;
    @Autowired
    LiveUserDaoService liveUserDaoService;
    @Autowired
    private BackOfficeUserRepository backOfficeUserRepository;
    @Autowired
    private RoomDaoService roomDaoService;
    @Autowired
    LiveLimitProcess liveLimitProcess;

    public boolean verifyIsLiveUser(LiveUser liveUser) {
        if (Objects.isNull(liveUser)) return false;
        return isOfficialLiveStatue(liveUser.getLiveStatus());
    }


    public boolean checkIsLiveUser(User user) {
        if (user != null && !StringUtils.isNullOrBlank(user.getLiveUserId())) {
            return verifyIsLiveUser(liveUserDaoService.findById(user.getLiveUserId()));
        }
        return false;
    }

    public Constant.ResultCode verifyReplyDiary(Jinli.ReplyDiaryRequest request, PersonDiary diary) {
        String diaryId = request.getDiaryId();
        String text = request.getText();
        if (Strings.isBlank(diaryId) || Strings.isBlank(text))
            return PARAM_ERROR;
        if (Objects.isNull(diary))
            return DIARY_NOTFOUND;
        if (text.length() > zoneConfigProperties.getReplyContentMaxSize())
            return REPLY_CONTENT_TOOLONG;
        if (wordFilterUtil.containSensitiveWord(text))
            return CONTENT_WORDS_ILLEGAL;
        return SUCCESS;
    }

    public boolean authUpload(UploadFileInfo uploadInfo) {
        if (Strings.isBlank(uploadInfo.getUserId())) {
            logger.warning(("upload param error" + uploadInfo));
            return false;
        }
        User user = dataManager.findUser(uploadInfo.getUserId());
        Constant.UploadHandlerType handlerType = Constant.UploadHandlerType.forNumber(Integer.parseInt(uploadInfo.getHandlerType()));
        if (Objects.isNull(user) || Objects.isNull(handlerType))
            return false;
        LiveUser liveUser;
        switch (handlerType) {
            case DIARY:
                liveUser = dataManager.findLiveUser(user.getLiveUserId());
                return verifyIsLiveUser(liveUser) && Objects.nonNull(uploadInfo.getExamParam(DIARYID));
            case LIVEUSERIMAGE:
                liveUser = liveUserDaoService.findByUserId(user.getId());
                return Objects.nonNull(liveUser);
            case UPDATEROOMIMAGE:
            case MUSIC:
                liveUser = dataManager.findLiveUser(user.getLiveUserId());
                return verifyIsLiveUser(liveUser);
            case UNRECOGNIZED:
        }
        return true;
    }

    public Constant.ResultCode checkUserHasBindPhoneAndModifyName(User user) {
        if (StringUtils.isNullOrBlank(user.getPhoneNumber())) {
            return NOT_BIND_PHONE;
        }
        if (user.getModifyNameCount() <= 0) {
            return NOT_MODIFY_DISPLAY_NAME;
        }
        if (user.isTourist())
            return NO_PASSWORD_SET;
        return SUCCESS;
    }

    public boolean isPlatformAccount(String userName) {
        BackOfficeUser backOfficeUser = backOfficeUserRepository.findByAccountName(userName);
        if (backOfficeUser.getRoles().contains(ADMIN)) return false;
        return backOfficeUser.getRoles().contains(PLATFORM) || backOfficeUser.getRoles().contains(PLATFORM_Q);
    }

    public static boolean verifyIsEmptyGameType(Constant.GameType gameType) {
        return Objects.equals(gameType, JIAOYOU) || Objects.equals(gameType, MENGCHONG)
                || Objects.equals(gameType, TIAOWU) || Objects.equals(gameType, CHANGGE)
                || Objects.equals(gameType, YANZHI);
    }

    public boolean checkLiveIsExist(String liveUserId) {
        LiveUser liveUser = dataManager.findLiveUser(liveUserId);
        if (Objects.isNull(liveUser) || StringUtils.isNullOrBlank(liveUser.getPlayingGameId())) return false;
        Room room = DataManager.findOnlineRoom(liveUser.getRoomId());
        if (Objects.isNull(room)) return false;
        CardGame game = DataManager.findGame(liveUser.getPlayingGameId());
        return game != null;
    }

    public Constant.ResultCode checkLiveUserAndPermissionParam(LiveUser liveUser, List<String> permissionNumbers) {
        for (String permission : permissionNumbers) {
            try {
                Constant.LiveUserPermission.valueOf(permission);
            } catch (IllegalArgumentException e) {
                return Constant.ResultCode.PERMISSION_NOT_EXIT;
            }
        }
        if (!verifyIsLiveUser(liveUser)) return Constant.ResultCode.LIVE_PLAYER_UNAPPROVED;
        return Constant.ResultCode.SUCCESS;
    }

    public boolean checkOfficialRoomIdIsUsedByNormalUser(String roomDisplayId) {
        Room room = roomDaoService.findByDisplayId(roomDisplayId);
        if (room != null) {
            Room onlineRoom = DataManager.findOnlineRoom(room.getId());
            if (Objects.nonNull(onlineRoom) && onlineRoom.isLive()) return true;
            LiveUser liveUser = liveUserDaoService.findById(room.getLiveUserId());
            return Objects.nonNull(liveUser) && !liveUser.isScriptLiveUser();
        }
        return false;
    }

    //============platform startLive check===================

    public Constant.ResultCode verifyStartLive(LiveUser liveUser, Jinli.StartLiveRequest request) {
        Constant.ResultCode resultCode;
        if (liveUser == null) return Constant.ResultCode.NOT_LIVE_USER;
        resultCode = verifyRequestInfo(request, liveUser);
        if (!Objects.equals(resultCode, SUCCESS)) {
            return resultCode;
        }
        else if (liveUser.getLiveStatus().equals(Constant.LiveStatus.LIVE_BAN))
            resultCode = Constant.ResultCode.LIVE_USER_IS_BAN;
        else if (!Objects.equals(Constant.LiveStatus.ONLINE,liveUser.getLiveStatus()) && !Objects.equals(Constant.LiveStatus.OFFLINE,liveUser.getLiveStatus()))
            resultCode = Constant.ResultCode.LIVE_PLAYER_UNAPPROVED;
        else if (!liveLimitProcess.allowLiveNow(liveUser))
            resultCode = Constant.ResultCode.LIVE_NOT_IN_ALLOW_TIME;
        return resultCode;
    }

    private Constant.ResultCode verifyRequestInfo(Jinli.StartLiveRequest request, LiveUser liveUser) {
        if (StringUtils.isNullOrBlank(request.getLiveDomain())) {
            logger.warning("liveDomain is empty");
            return PARAM_ERROR;
        }
        List<Jinli.LivePlatformParam> sharePlatformList = request.getLivePlatformParamsList();
        List<Constant.PlatformType> sharePlatformTypes = sharePlatformList.stream().filter(param -> !Objects.equals(liveUser.getPlatformType(), param.getPlatform()))
                .map(Jinli.LivePlatformParam::getPlatform).collect(Collectors.toList());
        if (!liveUser.getSharedPlatform().containsAll(sharePlatformTypes) || sharePlatformList.isEmpty()) {
            logger.warning("platform contains other not share platform");
            return LIVE_PLATFORM_LIMIT;
        }
        for (Jinli.LivePlatformParam livePlatformParam : sharePlatformList) {
            if (!VerifyUtil.verifyPlatformStartLive(livePlatformParam)) return PARAM_ERROR;
        }
        return SUCCESS;
    }

    public static boolean verifyPlatformStartLive(Jinli.LivePlatformParam livePlatformParam) {
        IPlatformService platformServiceByPlatform = PlatformServiceFactory.getPlatformServiceByPlatform(livePlatformParam.getPlatform());
        if (Objects.isNull(platformServiceByPlatform)) return false;
        return platformServiceByPlatform.verifyPlatformStartLive(livePlatformParam);
    }

    public static boolean isOfficialLiveStatue(Constant.LiveStatus liveStatus) {
        ArrayList<Constant.LiveStatus> OfficialLiveStatue = Lists.newArrayList(ONLINE, OFFLINE, LIVE_BAN);
        return OfficialLiveStatue.contains(liveStatus);
    }
}
