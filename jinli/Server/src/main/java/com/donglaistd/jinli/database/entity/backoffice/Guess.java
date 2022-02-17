package com.donglaistd.jinli.database.entity.backoffice;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.annotation.AutoIncKey;
import com.donglaistd.jinli.http.entity.GuessItem;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Document
public class Guess {
    @Id
    @AutoIncKey
    private long id;
    @Field
    private String title;
    @Field
    private String subtitle;
    @Field
    private String windowImg;
    @Field
    private String guessImg;
    @Field
    private List<GuessItem> itemList;
    @Field
    private Constant.GuessType guessType;
    @Field
    private Long showStartTime;
    @Field
    private Long showEndTime;
    @Field
    private Long wagerStartTime;
    @Field
    private Long wagerEndTime;
    @Field
    private int sort;
    @Field
    private Constant.GuessState state;
    @Field
    private Constant.GuessShow isShow;
    @Field
    private long total;
    @Field
    private long totalCoin;
    @Field
    private String win;
    @Field
    private boolean isSettle;

    public String getWin() {
        return win;
    }

    public void setWin(String win) {
        this.win = win;
    }

    public Constant.GuessShow getIsShow() {
        return isShow;
    }

    public void setIsShow(Constant.GuessShow isShow) {
        this.isShow = isShow;
    }

    public Long getTotalCoin() {
        return totalCoin;
    }

    public void setTotalCoin(Long totalCoin) {
        this.totalCoin = totalCoin;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Guess() {
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public void setWindowImg(String windowImg) {
        this.windowImg = windowImg;
    }

    public void setGuessImg(String guessImg) {
        this.guessImg = guessImg;
    }

    private Guess(String title, String subtitle, List<GuessItem> itemList,
                 Constant.GuessType guessType, Long showStartTime, Long showEndTime,
                 Long wagerStartTime, Long wagerEndTime) {
        this.title = title;
        this.subtitle = subtitle;
        this.itemList = itemList;
        this.guessType = guessType;
        this.showStartTime = showStartTime;
        this.showEndTime = showEndTime;
        this.wagerStartTime = wagerStartTime;
        this.wagerEndTime = wagerEndTime;
    }

    public void setGuessType(Constant.GuessType guessType) {
        this.guessType = guessType;
    }

    public void setGuessTypeName(String typeName){
        this.guessType = Constant.GuessType.valueOf(typeName);
    }

    public void setShowStartTime(Long showStartTime) {
        this.showStartTime = showStartTime;
    }

    public void setShowEndTime(Long showEndTime) {
        this.showEndTime = showEndTime;
    }

    public void setWagerStartTime(Long wagerStartTime) {
        this.wagerStartTime = wagerStartTime;
    }

    public void setWagerEndTime(Long wagerEndTime) {
        this.wagerEndTime = wagerEndTime;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public void setState(Constant.GuessState state) {
        this.state = state;
    }

    public static Guess newInstance(String title, String subtitle, List<GuessItem> itemList,
                                     Constant.GuessType guessType, Long showStartTime, Long showEndTime,
                                     Long wagerStartTime, Long wagerEndTime){
        return new Guess(title, subtitle, itemList, guessType, showStartTime, showEndTime, wagerStartTime, wagerEndTime);
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public String getWindowImg() {
        return windowImg;
    }

    public String getGuessImg() {
        return guessImg;
    }

    public List<GuessItem> getItemList() {
        return itemList;
    }

    public Constant.GuessType getGuessType() {
        return guessType;
    }

    public Long getShowStartTime() {
        return showStartTime;
    }

    public Long getShowEndTime() {
        return showEndTime;
    }

    public Long getWagerStartTime() {
        return wagerStartTime;
    }

    public Long getWagerEndTime() {
        return wagerEndTime;
    }

    public int getSort() {
        return sort;
    }

    public Constant.GuessState getState() {
        return state;
    }

    public boolean isSettle() {
        return isSettle;
    }

    public void becomeSettle() {
        isSettle = true;
    }

    public void setItemList(List<GuessItem> itemList) {
        this.itemList = itemList;
    }

    public String getId() {
        return String.valueOf(id);
    }

    public void setId(long id) {
        this.id = id;
    }
}
