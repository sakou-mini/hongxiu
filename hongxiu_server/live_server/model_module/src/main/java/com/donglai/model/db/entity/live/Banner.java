package com.donglai.model.db.entity.live;

import com.donglai.common.annotation.AutoIncKey;
import com.donglai.common.util.StringUtils;
import com.donglai.protocol.Common;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author Moon
 * @date 2021-12-20 16:09
 */
@Data
@NoArgsConstructor
@Document
public class Banner {


    @Id
    @AutoIncKey
    private long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 横幅链接
     */
    private String bannerUrl;

    /**
     * 跳转类型
     */
    private int jumpType;

    /**
     * 跳转链接
     */
    private String jumpUrl;

    /**
     * 排序
     */
    private int sort;

    /**
     * 状态  {} 0 待运行  1 运行中  2 已结束 3 已暂停}
     */
    private int status;

    /**
     * 开始时间
     */
    private long startTime;
    /**
     * 结束时间
     */
    private long endTime;
    /**
     * 创建人
     */
    private String createdId;
    /**
     * 创建时间
     */
    private long createdTime;
    /**
     * 修改时间
     */
    private long updateTime;


    public Banner(String title, String bannerUrl, Integer jumpType, String jumpUrl, int sort) {
        this.title = title;
        this.bannerUrl = bannerUrl;
        this.jumpType = jumpType;
        this.jumpUrl = jumpUrl;
        this.sort = sort;
        this.createdTime = System.currentTimeMillis();
    }

    public Common.Banner toProto() {
        Common.Banner.Builder builder = Common.Banner.newBuilder();
        builder.setBannerId(this.id);
        builder.setTitle(this.title);
        builder.setBannerUrl(this.bannerUrl);
        builder.setJumpType(this.jumpType);
        if(!StringUtils.isNullOrBlank(this.jumpUrl))
            builder.setJumpUrl(this.jumpUrl);
        return builder.build();
    }

    public boolean isOver(){
        return System.currentTimeMillis() > endTime;
    }
}
