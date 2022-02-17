package com.donglaistd.jinli.database.entity;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.util.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.*;
import java.util.stream.Collectors;

import static com.donglaistd.jinli.constant.GameConstant.MAX_MUTE_CHAT_RECORD;

@Document(collection = "blacklist")
public class Blacklist {
    @Id
    private ObjectId id = ObjectId.get();
    @Field
    @Indexed(unique = true)
    private String roomId;
    @Field
    private Set<String> mutes = new HashSet<>();
    @Field
    private Map<String, MuteProperty> muteChats = new HashMap<>();

    @Field
    private Set<UnMuteHistory> unMuteHistories = new HashSet<>(20);
    @Override
    public String toString() {
        return "Blacklist{" +
                "id=" + id +
                ", roomId='" + roomId + '\'' +
                ", mutes=" + mutes +
                ", muteChat=" + muteChats +
                '}';
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public Set<String> getMutes() {
        return mutes;
    }
    public void addMutes(String userId) {
        this.mutes.add(userId);
    }
    public void removeMutes(String userId) {
        this.mutes.remove(userId);
    }

    public void setMutes(Set<String> mutes) {
        this.mutes = mutes;
    }

    public static Blacklist getInstance(String roomId, String userId) {
        Blacklist instance = new Blacklist();
        instance.setRoomId(roomId);
        if(!StringUtils.isNullOrBlank(userId)){
            HashSet<String> mutes = new HashSet<>();
            mutes.add(userId);
            instance.setMutes(mutes);
        }
        return instance;
    }

    public Blacklist() {
    }

    public boolean containsMute(String userId) {
        return this.mutes.contains(userId);
    }

    public boolean containsMuteChat(String userId) {
        MuteProperty muteProperty = this.muteChats.get(userId);
        return Objects.nonNull(muteProperty) && muteProperty.isMute();
    }

    public void addMuteChat(String userId,MuteProperty muteProperty){
        this.muteChats.put(userId, muteProperty);
    }
    public void addMuteChat(String userId, Constant.MuteTimeType muteTimeType, Constant.MuteReason reason, String customReason, Constant.MuteIdentity muteIdentity, String optUserId, Constant.MuteArea muteArea){
        MuteProperty muteProperty = MuteProperty.newInstance(muteTimeType, reason, "", muteIdentity, optUserId, muteArea);
        addMuteChat(userId, muteProperty);
    }

    public void removeMuteChat(String userId){
        MuteProperty muteProperty = this.muteChats.remove(userId);
        if(Objects.nonNull(muteProperty)){
            addUnMuteHistory(userId,muteProperty);
        }
    }

    public MuteProperty getMuteChatProperty(String userId){
        return muteChats.get(userId);
    }

    public Map<String, MuteProperty> getMuteChats() {
        return muteChats;
    }

    public void addUnMuteHistory(String userId,MuteProperty muteProperty){
        if(unMuteHistories.size() >= MAX_MUTE_CHAT_RECORD){
            UnMuteHistory oldUnMuteHistory = unMuteHistories.stream().min(Comparator.comparing(history -> history.getMuteProperty().getMuteStartTime())).orElse(null);
            unMuteHistories.remove(oldUnMuteHistory);
        }
        unMuteHistories.add(UnMuteHistory.newInstance(userId, muteProperty));
    }

    public void updateUnMuteHistory() {
        List<Map.Entry<String, MuteProperty>> unMuteHistory = this.muteChats.entrySet().stream().filter(entry -> !entry.getValue().isMute()).collect(Collectors.toList());
        unMuteHistory.forEach( entry ->{
            this.muteChats.remove(entry.getKey());
            addUnMuteHistory(entry.getKey(), entry.getValue());
        });
    }

    public List<UnMuteHistory> getUnMuteHistories() {
        return unMuteHistories.stream().sorted(Comparator.comparing(history -> history.getMuteProperty().getMuteStartTime())).collect(Collectors.toList());
    }
}
