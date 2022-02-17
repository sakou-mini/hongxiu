package com.donglaistd.jinli.processors.handler;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.Jinli;
import com.donglaistd.jinli.annotation.IgnoreAuth;
import com.donglaistd.jinli.annotation.IgnoreShutDown;
import com.donglaistd.jinli.database.dao.LiveUserDaoService;
import com.donglaistd.jinli.database.dao.RoomDaoService;
import com.donglaistd.jinli.database.dao.UserAttributeDaoService;
import com.donglaistd.jinli.database.entity.LiveUser;
import com.donglaistd.jinli.database.entity.Room;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.UserAttribute;
import com.donglaistd.jinli.database.entity.game.BankerGame;
import com.donglaistd.jinli.database.entity.game.CardGame;
import com.donglaistd.jinli.database.entity.race.RaceBase;
import com.donglaistd.jinli.database.entity.race.UserRace;
import com.donglaistd.jinli.http.entity.TokenRequestResult;
import com.donglaistd.jinli.service.*;
import com.donglaistd.jinli.service.statistic.UserDataStatisticsProcess;
import com.donglaistd.jinli.util.DataManager;
import com.donglaistd.jinli.util.StringUtils;
import com.donglaistd.jinli.util.VerifyUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.logging.Logger;

import static com.donglaistd.jinli.Constant.AccountStatue.ACCOUNT_BAN;
import static com.donglaistd.jinli.Constant.ResultCode.*;
import static com.donglaistd.jinli.constant.CacheNameConstant.getPlatformTokenAccountKey;
import static com.donglaistd.jinli.constant.GameConstant.PLATFORMUSER_SPLITTER;
import static com.donglaistd.jinli.util.MessageUtil.buildReply;
import static com.donglaistd.jinli.util.MessageUtil.sendMessage;

@IgnoreShutDown
@IgnoreAuth
@Component
public class LoginRequestHandler extends MessageHandler {
    private static final Logger logger = Logger.getLogger(LoginRequestHandler.class.getName());
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    LiveUserDaoService liveUserDaoService;
    @Autowired
    RoomDaoService roomDaoService;
    @Autowired
    DataManager dataManager;
    @Autowired
    TaskProcess taskProcess;
    @Autowired
    private RedisTemplate<String, TokenRequestResult> redisTemplate;
    @Autowired
    GameRecoverService gameRecoverService;
    @Autowired
    UserOperationService userOperationService;
    @Autowired
    UserDataStatisticsProcess userDataStatisticsProcess;
    @Autowired
    UserAttributeDaoService userAttributeDaoService;
    @Autowired
    DiaryProcess diaryProcess;
    @Autowired
    DomainProcess domainProcess;
    @Autowired
    VerifyUtil verifyUtil;
    @Autowired
    LoginProcess loginProcess;
    @Autowired
    UserProcess userProcess;

    @Override
    public synchronized Jinli.JinliMessageReply doHandle(ChannelHandlerContext ctx, Jinli.JinliMessageRequest messageRequest, User user) {
        resultCode = SUCCESS;
        var request = messageRequest.getLoginRequest();
        var accountName = request.getAccount();
        var password = request.getPassword();
        var reply = Jinli.LoginReply.newBuilder();
        var userInfoBuilder = Jinli.UserInfo.newBuilder();
        user = loginProcess.findUserByAccount(accountName);
        if (user == null) {
            logger.info("user logged failed, not exist:" + accountName);
            return buildReply(reply.setUserInfo(userInfoBuilder.build()), USER_NOT_FOUND);
        }else if (!checkToken(user, password)) {
            logger.info("user logged failed by incorrect password:" + accountName);
            return buildReply(reply.setUserInfo(userInfoBuilder.build()), PASSWORD_ERROR);
        }
        UserAttribute userAttribute = userAttributeDaoService.findByUserIdOrSaveIfNotExit(user.getId());
        if(userAttribute != null && Objects.equals(ACCOUNT_BAN,userAttribute.getStatue())){
            logger.info("user logged failed account is Ban:" + accountName);
            return buildReply(reply.setUserInfo(userInfoBuilder.build()), ACCOUNT_HAS_BAN);
        }
        dealLoginSuccess(user, ctx);
        recoverUserLastLiveRecord(ctx, user,reply);
        recoverRace(user, reply);
        Constant.UserType userType = userOperationService.getUserType(user);
        userInfoBuilder.setUserId(String.valueOf(user.getId())).setLevel(user.getLevel()).setVipId(user.getVipType())
                .setDisplayName(user.getDisplayName()).setUserType(userType).setGameCoin(user.getGameCoin())
                .setAvatarUrl(user.getAvatarUrl()).setGoldBean(String.valueOf(user.getGoldBean()))
                .setPlatformType(user.getPlatformType());
        if (Objects.nonNull(user.getPhoneNumber())) userInfoBuilder.setPhoneNumber(user.getPhoneNumber());
        user.setLastLoginTime(System.currentTimeMillis());
        user.setLastMobileModel(request.getMobileModel());
        user.setLastIp(dataManager.getUserRemoteAddress(user.getId()));
        dataManager.saveUser(user);
        if(userAttribute.addIpHistory(user.getLastIp())){
            userAttributeDaoService.save(userAttribute);
        }
        taskProcess.updateUserTask(user.getId());
        userDataStatisticsProcess.totalLoginUserDataStatistics(request,user);
        diaryProcess.removeUserDiaryHistory(user.getId());
        domainProcess.saveDomainViewRecord(user.getId(),request.getDomainName(),user.getPlatformType(),ctx);
        String session = loginProcess.generatedAndSaveUserSession(user);
        logger.info("user登录--->" + user.getId() +"更新后的session:"+session);
        return buildReply(reply.setUserInfo(userInfoBuilder.build()).setClientSession(session), resultCode);
    }

    private boolean checkToken(User user, String password) {
        String accountName = user.getAccountName();
        if (accountName.contains(PLATFORMUSER_SPLITTER)) {
            var tokenResult = redisTemplate.opsForValue().get(getPlatformTokenAccountKey(accountName));
            return tokenResult != null && password.equals(tokenResult.getToken());
        }
        if(StringUtils.isNullOrBlank(user.getToken()))
            return true;
        passwordEncoder.encode(user.getToken());
        return passwordEncoder.matches(password, user.getToken());
    }

    private void dealLoginSuccess(User user, ChannelHandlerContext ctx) {
        Channel oldConnected = checkAndKickOutOldConnected(ctx, user);
        if(oldConnected!=null) oldConnected.disconnect();
        logger.info("user logged in as:" + user.getAccountName());
        resultCode = Constant.ResultCode.SUCCESS;
        dataManager.putUserChannel(user.getId(), ctx.channel());
        user.setOnline(true);
        dataManager.saveUser(user);
        DataManager.disconnectUser.remove(user.getId());
        DataManager.removeUserDisconnectTask(user.getId());
        DataManager.removeEndLiveTask(user.getId());
        DataManager.saveUserKeyToChannel(ctx, user.getId());
        userProcess.quitRaceIfNotStart(dataManager.findOnlineUser(user.getId()));
        userProcess.quitRoomIfHasEnterRoom(dataManager.findOnlineUser(user.getId()));
    }

    private Channel checkAndKickOutOldConnected(ChannelHandlerContext ctx,User user){
        Channel oldChannel = DataManager.getUserChannel(user.getId());
        if(Objects.nonNull(oldChannel) && !oldChannel.equals(ctx.channel()) && oldChannel.isActive()){
            sendMessage(oldChannel, buildReply(Jinli.RepeatLoginBroadcastMessage.newBuilder().build()));
            logger.info(user.getId()+ "重复登录,踢出旧连接");
            return oldChannel;
        }
        return null;
    }



    private void recoverUserLastLiveRecord(ChannelHandlerContext ctx,User user,Jinli.LoginReply.Builder loginReply){
        boolean isRecover = dealRecoverBankerIfExist(user,loginReply);
        if(verifyUtil.checkIsLiveUser(user)){
            Room room = roomDaoService.findByLiveUser(dataManager.findLiveUser(user.getLiveUserId()));
            DataManager.saveRoomKeyToChannel(ctx, room.getId());
            isRecover = dealRecoverLiveUserLive(user,loginReply);
        }
        String enterRoomLiveUserId = dataManager.getUserEnterRoomRecord(user.getId());
        if(!isRecover && !StringUtils.isNullOrBlank(enterRoomLiveUserId)){
            gameRecoverService.recoverLiveGame(user,enterRoomLiveUserId,loginReply);
        }
    }
    private boolean dealRecoverBankerIfExist(User user,Jinli.LoginReply.Builder loginReply) {
        String liveUserId = dataManager.getUserEnterRoomRecord(user.getId());
        LiveUser liveUser = dataManager.findLiveUser(liveUserId);
        if(Objects.isNull(liveUser) || StringUtils.isNullOrBlank(liveUser.getPlayingGameId())) return false;
        Room room = DataManager.findOnlineRoom(liveUser.getRoomId());
        CardGame game = DataManager.findGame(liveUser.getPlayingGameId());
        if (Objects.nonNull(room) && Objects.nonNull(game) && game instanceof BankerGame) {
            BankerGame bankerGame = (BankerGame) game;
            if (Objects.equals(bankerGame.getBanker(), user)) {
                var bankerReconnect = gameRecoverService.generateReconnectLiveMessage(room, liveUser, bankerGame);
                loginReply.setBankerReconnect(bankerReconnect);
                return true;
            }
        }
        return false;
    }

    private boolean dealRecoverLiveUserLive(User user, Jinli.LoginReply.Builder loginReply) {
        LiveUser liveUser = dataManager.findLiveUser(user.getLiveUserId());
        var room = DataManager.findOnlineRoom(liveUser.getRoomId());
        if (Objects.nonNull(room) && room.isLive()) {
            if (liveUser.getLiveStatus() == Constant.LiveStatus.ONLINE && Strings.isNotBlank(liveUser.getPlayingGameId())) {
                logger.info("recover game info:" + user.getId());
                CardGame game = DataManager.findGame(liveUser.getPlayingGameId());
                if(Objects.isNull(game)) return false;
                Jinli.ReconnectLiveMessage liveUserReconnect = gameRecoverService.generateReconnectLiveMessage(room, liveUser, game);
                loginReply.clearBankerReconnect();
                loginReply.setLiveUserReconnect(liveUserReconnect);
                return true;
            }
        }
        return false;
    }

    private void recoverRace(User user, Jinli.LoginReply.Builder reply){
        UserRace userRace = DataManager.findUserRace(user.getId());
        if(Objects.isNull(userRace)) return;
        RaceBase race = DataManager.findRace(userRace.getRaceId());
        if(race!=null){
            logger.info("recover Race!");
            reply.setUserRace(Jinli.UserRace.newBuilder().setJoinedRaceId(race.getId()).setRaceType(race.getRaceType()));
        }
    }
}
