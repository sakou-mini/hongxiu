package com.donglai.model.db.entity.common;

import com.alibaba.fastjson.annotation.JSONField;
import com.donglai.common.annotation.AutoIncKey;
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
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;

import static com.donglai.common.constant.PathConstant.DEFAULT_AVATAR_PATH;
import static com.donglai.common.constant.PathConstant.DEFAULT_BACKGROUND_PATH;


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
    private boolean firstLogin = true;
    private String avatarUrl = DEFAULT_AVATAR_PATH;
    private long createTime;
    private String mobileCode;
    @Indexed(unique = true, sparse = true)
    private String phoneNumber;
    private AtomicLong coin = new AtomicLong(0);

    private long experience;
    private Constant.VipType vipLevel = Constant.VipType.LOCKED;
    private long lastLoginTime;
    private String lastLoginIp;
    private long logoutTime;
    private Constant.UserType userType = Constant.UserType.NORMAL;
    private String signatureText;
    private String backgroundImage = DEFAULT_BACKGROUND_PATH;
    private boolean tourist = false;
    private Constant.GenderType gender = Constant.GenderType.FEMALE;
    private long birthday;
    private String region;
    private String school;
    private String hometown;
    private boolean officialUser; //是否官方用户
    private boolean status;
    private long updateTime;
    private String reason; //用户状态说明
    private Long firstLoginTime;
    private String firstLoginIp;
    private Constant.PlatformType platform = Constant.PlatformType.HONG_XIU;
    /**
     * 积分
      */
    private int integral;
    /**
     * 等级
     */
    private int level;

    public User(String accountId, String nickname, String password, String mobileCode) {
        this.accountId = accountId;
        this.nickname = nickname;
        this.password = password;
        this.mobileCode = mobileCode;
        this.createTime = System.currentTimeMillis();
    }


    public User(String accountId, String nickname, String uuid, String source, String password, String phoneNumber, Boolean status) {
        this.accountId = accountId;
        this.nickname = nickname;
        this.uuid = uuid;
        this.source = source;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.status = status;
    }

    @JSONField(serialize = false)
    public long getCoinNumber() {
        return coin.longValue();
    }

    @JSONField(serialize = false)
    public boolean isLiveUser() {
        return userType.equals(Constant.UserType.TYPE_LIVEUSER) || userType.equals(Constant.UserType.TYPE_LIVEUSER_APPROVED);
    }

    public void becomeLiveUser(String liveUserId) {
        this.liveUserId = liveUserId;
        this.userType = Constant.UserType.TYPE_LIVEUSER;
    }

    public void addCoin(long coin) {
        if (coin > 0) this.coin.addAndGet(coin);
    }

    public void descCoin(long coin) {
        if (coin > 0) this.coin.addAndGet(-coin);
    }

    public Common.UserInfo toSummaryProto() {
        Common.UserInfo.Builder build = Common.UserInfo.newBuilder().setUserId(getId())
                .setAccountId(accountId).setAvatarUrl(avatarUrl).setLevel(level)
                .setNickname(nickname).setCoin(coin.longValue()).setUserType(userType)
                .setIsTourist(tourist).setVipLevel(vipLevel).setGender(gender)
                .setPlatform(platform).setFirstLogin(firstLogin);
        if (!StringUtils.isNullOrBlank(phoneNumber)) build.setPhoneNumber(phoneNumber);
        return build.build();
    }

    public Common.UserInfo toSimpleProto() {
        return Common.UserInfo.newBuilder().setUserId(getId())
                .setAccountId(accountId).setAvatarUrl(avatarUrl).setLevel(level)
                .setNickname(nickname).setVipLevel(vipLevel).setPlatform(platform).build();
    }

    public Common.UserInfo toDetailProto() {
        var builder = toSummaryProto().toBuilder().setBirthday(birthday);
        if (!StringUtils.isNullOrBlank(signatureText))
            builder.setSignatureText(signatureText);
        if (!StringUtils.isNullOrBlank(region))
            builder.setRegion(region);
        if (!StringUtils.isNullOrBlank(backgroundImage)) {
            builder.setBackgroundImage(backgroundImage);
        }
        if (!StringUtils.isNullOrBlank(school)) {
            builder.setSchool(school);
        }
        if (!StringUtils.isNullOrBlank(hometown)) {
            builder.setHometown(hometown);
        }
        return builder.build();
    }

    public boolean isOnline() {
        return this.lastLoginTime > this.logoutTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) &&
                Objects.equals(accountId, user.accountId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, accountId);
    }
}
