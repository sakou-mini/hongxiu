package com.donglaistd.jinli.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@PropertySource(value = {"classpath:zone_config.properties"})
public class ZoneConfigProperties {
    @Value("${image.max.num}")
    private int imageMaxNum;

    @Value("${video.max.num}")
    private int videoMaxNum;
    @Value("${data.zone.image.save_path}")
    private String bgImagePath;
    @Value("${data.zone.image.max_width}")
    private int bgImageMaxWidth;
    @Value("${data.zone.image.max_height}")
    private int bgImageMaxHeight;
    @Value("${data.zone.signature.max_size}")
    private int signatureMaxSize;
    @Value("${data.zone.diary.content.max_size}")
    private int diaryContentMaxSize;
    @Value("${data.zone.diary.max.num}")
    private int diaryMaxNum;
    @Value("${data.reply.content.max_size}")
    private int replyContentMaxSize;
    @Value("${data.diary.expire_time}")
    private long diaryIdExpireTime;
    @Value("${data.diary.topic_num}")
    private int diaryTopicNum;

    public int getImageMaxNum() {
        return imageMaxNum;
    }

    public int getVideoMaxNum() {
        return videoMaxNum;
    }

    public String getBgImagePath() {
        return bgImagePath;
    }

    public int getBgImageMaxWidth() {
        return bgImageMaxWidth;
    }

    public int getBgImageMaxHeight() {
        return bgImageMaxHeight;
    }

    public int getSignatureMaxSize() {
        return signatureMaxSize;
    }

    public int getDiaryContentMaxSize() {
        return diaryContentMaxSize;
    }

    public int getDiaryMaxNum() {
        return diaryMaxNum;
    }

    public int getReplyContentMaxSize() {
        return replyContentMaxSize;
    }

    public long getDiaryIdExpireTime() {
        return diaryIdExpireTime;
    }

    public int getDiaryTopicNum() {
        return diaryTopicNum;
    }
}
