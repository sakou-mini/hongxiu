package com.donglaistd.jinli.database.entity.system;

import com.donglaistd.jinli.Constant;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GiftConfig {
    @Id
    private ObjectId id = ObjectId.get();
    private String giftId;
    private long price;
    private int priceType;
    private String name;
    private String image;
    private String className;
    private int animationRequirement;
    private boolean fullScreen;
    private boolean luxury;
    private offset offset;
    private int sortId;
    private boolean enable = true;
    private int scale;
    private Constant.PlatformType platformType;

    public GiftConfig() {
    }

    @JsonCreator
    public GiftConfig(@JsonProperty("id") String giftId, @JsonProperty("price") long price,
                      @JsonProperty("priceType") int priceType, @JsonProperty("name") String name,
                      @JsonProperty("image") String image, @JsonProperty("class") String className,
                      @JsonProperty("animationRequirement") int animationRequirement,
                      @JsonProperty("fullScreen") boolean fullScreen, @JsonProperty("luxury") boolean luxury,
                      @JsonProperty("offset") GiftConfig.offset offset,
                      @JsonProperty("scale") int scale) {
        this.giftId = giftId;
        this.price = price;
        this.priceType = priceType;
        this.name = name;
        this.image = image;
        this.className = className;
        this.animationRequirement = animationRequirement;
        this.fullScreen = fullScreen;
        this.luxury = luxury;
        this.offset = offset;
        this.scale = scale;
    }

    public String getGiftId() {
        return String.valueOf(giftId);
    }

    public void setGiftId(String giftId) {
        this.giftId = giftId;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public int getAnimationRequirement() {
        return animationRequirement;
    }

    public void setAnimationRequirement(int animationRequirement) {
        this.animationRequirement = animationRequirement;
    }

    public boolean isFullScreen() {
        return fullScreen;
    }

    public void setFullScreen(boolean fullScreen) {
        this.fullScreen = fullScreen;
    }

    public GiftConfig.offset getOffset() {
        return offset;
    }

    public void setOffset(GiftConfig.offset offset) {
        this.offset = offset;
    }

    public int getSortId() {
        return sortId;
    }

    public void setSortId(int sortId) {
        this.sortId = sortId;
    }

    public int getPriceType() {
        return priceType;
    }

    public void setPriceType(int priceType) {
        this.priceType = priceType;
    }

    public Constant.GiftPriceType getGiftPriceType(){
        return Constant.GiftPriceType.forNumber(priceType);
    }

    public boolean isLuxury() {
        return luxury;
    }

    public void setLuxury(boolean luxury) {
        this.luxury = luxury;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public int getScale() {
        return scale;
    }

    public void setScale(int scale) {
        this.scale = scale;
    }

    public Constant.PlatformType getPlatformType() {
        return platformType;
    }

    public void setPlatformType(Constant.PlatformType platformType) {
        this.platformType = platformType;
    }

    public static class offset{
        @JsonProperty("x")
        public double x;
        @JsonProperty("y")
        public double y;

        public double getX() {
            return x;
        }

        public void setX(double x) {
            this.x = x;
        }

        public double getY() {
            return y;
        }

        public void setY(double y) {
            this.y = y;
        }
    }

    @JsonIgnore
    public boolean isCoinGift(){
        return Objects.equals(Constant.GiftPriceType.GIFT_PRICE_GAMECOIN, getGiftPriceType());
    }
}
