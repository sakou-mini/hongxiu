package com.donglai.model.db.entity.live;

import com.donglai.protocol.Constant;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@JsonIgnoreProperties(ignoreUnknown = true)
@NoArgsConstructor
@Data
public class GiftInfo {
    @Id
    private String id;
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


    public GiftInfo(String id, long price, int priceType, String name, String image, String className,
                    int animationRequirement, boolean fullScreen, boolean luxury,
                    GiftInfo.offset offset, int sortId, boolean enable, int scale) {
        this.id = id;
        this.price = price;
        this.priceType = priceType;
        this.name = name;
        this.image = image;
        this.className = className;
        this.animationRequirement = animationRequirement;
        this.fullScreen = fullScreen;
        this.luxury = luxury;
        this.offset = offset;
        this.sortId = sortId;
        this.enable = enable;
        this.scale = scale;
    }


    public Constant.GiftPriceType getGiftPriceType(){
        return Constant.GiftPriceType.forNumber(priceType);
    }

    @Data
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
}
