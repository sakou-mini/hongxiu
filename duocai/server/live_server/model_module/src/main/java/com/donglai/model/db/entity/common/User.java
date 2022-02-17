package com.donglai.model.db.entity.common;

import com.alibaba.fastjson.annotation.JSONField;
import com.donglai.common.annotation.AutoIncKey;
import com.donglai.common.constant.UserStatus;
import com.donglai.common.util.StringUtils;
import com.donglai.protocol.Common;
import com.donglai.protocol.Constant;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicLong;

import static com.donglai.common.constant.PathConstant.DEFAULT_AVATAR_PATH;


@Data
@NoArgsConstructor
@Document(collection = "user")
public class User implements Serializable {
    @Id
    @AutoIncKey
    private String id;
    @Field
    @Indexed(unique = true, sparse = true)
    private String liveUserId;
    @Field
    @Indexed(unique = true)
    private String accountId;
    @Indexed(unique = true, sparse = true)
    private String otherId; //其他id
    @Field
    private String uuid;
    private String source;
    private String nickname;
    private String password;
    private String avatarUrl = DEFAULT_AVATAR_PATH;
    private long createTime;
    private String mobileCode;
    @Indexed(unique = true, sparse = true)
    private String phoneNumber;
    private AtomicLong coin = new AtomicLong(0);
    private int level = 1;
    private long experience;
    private Constant.VipType vipLevel = Constant.VipType.LOCKED;
    private long lastLoginTime;
    private long logoutTime;
    private Constant.UserType userType =  Constant.UserType.NORMAL;
    private String signatureText;
    private String backgroundImage;
    private boolean tourist = true;
    private Constant.GenderType gender = Constant.GenderType.Gender_NULL;
    private long birthday;
    private String region;
    private boolean officialUser; //是否官方用户
    private Constant.PlatformType platform = Constant.PlatformType.DUOCAI;
    private int status = UserStatus.NORMAL.getValue();  //0 正常 1 封禁
    private String ip;

    public User(String accountId, String nickname, String password, String mobileCode) {
        this.accountId = accountId;
        this.nickname = nickname;
        this.password = password;
        this.mobileCode = mobileCode;
        this.createTime = System.currentTimeMillis();
    }

    public User(String accountId, String nickname, String uuid, String source, String password, String phoneNumber) {
        this.accountId = accountId;
        this.nickname = nickname;
        this.uuid = uuid;
        this.source = source;
        this.password = password;
        this.phoneNumber = phoneNumber;
    }

    @JSONField(serialize = false)
    public long getCoinNumber(){
        return coin.longValue();
    }
    @JSONField(serialize = false)
    public boolean isLiveUser(){
        return userType.equals(Constant.UserType.TYPE_LIVEUSER) || userType.equals(Constant.UserType.TYPE_LIVEUSER_APPROVED);
    }

    public void becomeLiveUser(String liveUserId){
        this.liveUserId = liveUserId;
        this.userType = Constant.UserType.TYPE_LIVEUSER;
    }

    public void addCoin(long coin) {
        if(coin>0) this.coin.addAndGet(coin);
    }

    public void descCoin(long coin) {
        if(coin>0) this.coin.addAndGet(-coin);
    }

    public Common.UserInfo toSummaryProto(){
        Common.UserInfo.Builder build = Common.UserInfo.newBuilder().setUserId(getId())
                .setAccountId(accountId).setAvatarUrl(avatarUrl).setLevel(level)
                .setNickname(nickname).setCoin(coin.longValue()).setUserType(userType)
                .setIsTourist(tourist).setVipLevel(vipLevel).setGender(gender).setPlatform(platform);
        if(!StringUtils.isNullOrBlank(phoneNumber)) build.setPhoneNumber(phoneNumber);
        return build.build();
    }

    public Common.UserInfo toSimpleProto(){
        return Common.UserInfo.newBuilder().setUserId(getId())
                .setAccountId(accountId).setAvatarUrl(avatarUrl).setLevel(level)
                .setNickname(nickname).setVipLevel(vipLevel).setPlatform(platform).build();
    }

    public Common.UserInfo toDetailProto(){
        var builder = toSummaryProto().toBuilder().setBirthday(birthday);
        if(!StringUtils.isNullOrBlank(signatureText))
            builder.setSignatureText(signatureText);
        if(!StringUtils.isNullOrBlank(region))
            builder.setRegion(region);
        if(!StringUtils.isNullOrBlank(backgroundImage)){
            builder.setBackgroundImage(backgroundImage);
        }
        return builder.build();
    }
    public boolean isOnline(){
        return this.lastLoginTime > this.logoutTime;
    }
}
