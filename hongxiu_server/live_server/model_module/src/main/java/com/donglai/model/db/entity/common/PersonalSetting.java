package com.donglai.model.db.entity.common;

import com.donglai.protocol.Common;
import com.donglai.protocol.Constant;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.HashSet;
import java.util.Set;

import static com.donglai.protocol.Constant.PermissionVisibleType.PERMISSION_ALL;


@Data
@Document
@NoArgsConstructor
public class PersonalSetting {
    @Id
    private String userId;
    //=======隐私设置==============
    @Field
    private Constant.PermissionVisibleType commentPermission = PERMISSION_ALL; //评论权限
    @Field
    private Constant.PermissionVisibleType privateChatPermission = PERMISSION_ALL; //私信权限
    @Field
    private boolean showFansAndLeaderList = true; //显示粉丝和关注列表
    @Field
    private boolean showMyBlogsPraise = true; //显示我的点赞作品（不可见时只能看到点赞数量）

    @Field
    private Set<String> blackList = new HashSet<>();

    public void addBlackList(String userId) {
        blackList.add(userId);
    }

    public PersonalSetting(String userId) {
        this.userId = userId;
    }

    public static PersonalSetting newInstance(String userId) {
        return new PersonalSetting(userId);
    }

    public Common.PersonalSetting toProto() {
        return Common.PersonalSetting.newBuilder()
                .setCommentPermission(commentPermission)
                .setPrivateChatPermission(privateChatPermission)
                .setShowFansAndLeaderList(showFansAndLeaderList).setShowMyBlogsPraise(showMyBlogsPraise).build();
    }
}
