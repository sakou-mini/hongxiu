package com.donglai.model.db.entity.live;

import com.donglai.common.util.VerifyUtil;
import com.donglai.protocol.Constant;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.donglai.protocol.Constant.LiveUserStatus.LIVE_OFFLINE;

@Document
@Data
public class LiveUser implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    private String id;
    @Field
    @Indexed(unique = true)
    private String userId;
    @Field
    @Indexed(unique = true, sparse = true)
    private String roomId;
    private int level = 1;
    private long exp;
    private Constant.GenderType gender = Constant.GenderType.Gender_NULL;
    private String idImageURL;
    private List<String> images = new ArrayList<>();
    private String country;
    private String address;
    private String email;
    private String contactWay;
    private long birthDay;
    private long lastLiveTime;
    private String bankName;
    private String bankCard;
    private long applyTime;
    private long auditTime;
    private String realName;
    private String phoneNumber;
    private Constant.LiveUserStatus status = LIVE_OFFLINE;
    private Constant.PlatformType platform = Constant.PlatformType.HONG_XIU;
    private List<Constant.PlatformType> slavePlatforms = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LiveUser liveUser = (LiveUser) o;
        return id.equals(liveUser.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public boolean isPassLiveUser() {
        return VerifyUtil.isPassLiveUserStatus(status);
    }
}
