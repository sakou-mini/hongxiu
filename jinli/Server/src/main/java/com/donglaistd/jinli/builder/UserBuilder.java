package com.donglaistd.jinli.builder;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.dao.UserAttributeDaoService;
import com.donglaistd.jinli.database.dao.UserDaoService;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.http.service.CoinFlowService;
import com.donglaistd.jinli.util.DataManager;
import com.donglaistd.jinli.util.IdGeneratedProcess;
import com.donglaistd.jinli.util.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

import static com.donglaistd.jinli.util.StringUtils.generateTouristName;

@Component
public class UserBuilder {

    public static final int USER_ID_LENGTH = 7;

    @Value("${tourist.name.prefix}")
    private String touristNamePrefix;

    @Autowired
    DataManager dataManager;
    @Autowired
    UserDaoService userDaoService;
    @Autowired
    CoinFlowService coinFlowService;
    @Autowired
    IdGeneratedProcess idGeneratedProcess;
    @Autowired
    UserAttributeDaoService userAttributeDaoService;

    private User createInitUser(String accountName,String displayName,
                                String avatarUrl,String token,
                                boolean isTourist,String phoneNumber,
                                long gameCoin,Constant.PlatformType platform,
                                String mobileCode,boolean scriptUser){
        User user = new User();
        user.setAccountName(accountName);
        user.setDisplayName(displayName);
        user.setAvatarUrl(avatarUrl);
        user.setToken(token);
        user.setTourist(isTourist);
        user.setCreateDate(new Date());
        user.setPhoneNumber(phoneNumber);
        user.setGameCoin(gameCoin);
        user.setPlatformType(platform);
        user.setMobileCode(mobileCode);
        user.setScriptUser(scriptUser);
        return user;
    }

    private synchronized User createOriginUser(String accountName, String displayName, String avatarUrl, String token, long gameCoin, String phoneNumber,
                                        boolean isTourist, Constant.PlatformType platform,String mobileCode,boolean scriptUser,String platformUserId) {

        String id = idGeneratedProcess.generatedId(User.class.getSimpleName(), USER_ID_LENGTH);
        if(StringUtils.isNullOrBlank(displayName)){
            displayName = touristNamePrefix +id;
        }
        User user = createInitUser(accountName,displayName,avatarUrl,token,isTourist,phoneNumber,gameCoin,platform,mobileCode,scriptUser);
        user.setId(id);
        if(!StringUtils.isNullOrBlank(platformUserId)) user.setPlatformUserId(platformUserId);
        userAttributeDaoService.findByUserIdOrSaveIfNotExit(user.getId());
        return dataManager.saveUser(user);
    }

    public User createRegisterUser(String accountName,String displayName,String token,long gameCoin,String mobileCode, boolean isTourist) {
        return createOriginUser(accountName, displayName, "", token, gameCoin, "", isTourist,Constant.PlatformType.PLATFORM_JINLI,mobileCode,false,"");
    }

    public User createUser(String accountName,String displayName,String avatarUrl,String token,boolean isTourist) {
        return createOriginUser(accountName, displayName, avatarUrl, token, 0, "", isTourist,Constant.PlatformType.PLATFORM_JINLI,"",false,"");
    }

    public User createUser(String accountName,String displayName,String token){
        return createOriginUser(accountName, displayName, "", token, 0, "", true, Constant.PlatformType.PLATFORM_JINLI, "",false,"");
    }

    public User createPlatformUser(String accountName,String displayName,String token,long gameCoin, Constant.PlatformType platform,String platformUserId){
        return createOriginUser(accountName, displayName, "", token, gameCoin, "", true, platform, "",false,platformUserId);
    }

    public User createOfficialUser(String accountName,String displayName,String avatarUrl, long gameCoin){
        return createOriginUser(accountName, displayName, avatarUrl, "", gameCoin, "", true, Constant.PlatformType.PLATFORM_JINLI, "",true,"");
    }


    //============ create  unit test user ===================
    public User createNoSavedUser(String accountName, String displayName, String avatarUrl, String token, long gameCoin, String phoneNumber, boolean isTourist){

        User user = createInitUser(accountName,displayName,avatarUrl,token,isTourist,phoneNumber,gameCoin, Constant.PlatformType.PLATFORM_JINLI,"",false);
        user.setId(ObjectId.get().toString());
        user.setGameCoin(gameCoin);
        user.setPhoneNumber(phoneNumber);
        return user;
    }

    public User createNoSavedUser(String accountName,String displayName,String token){
        User user =  createInitUser(accountName,displayName,"",token,true,"",0, Constant.PlatformType.PLATFORM_JINLI,"",false);
        user.setId(ObjectId.get().toString());
        return user;
    }

}
