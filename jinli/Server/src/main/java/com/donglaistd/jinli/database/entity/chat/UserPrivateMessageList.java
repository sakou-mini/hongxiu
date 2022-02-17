package com.donglaistd.jinli.database.entity.chat;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.*;

import static com.donglaistd.jinli.constant.GameConstant.MAX_CONTACT_RECORD_NUM;

@Document
public class UserPrivateMessageList {
    @Id
    public String id;

    @Field
    private  List<String> contactList = new ArrayList<>();

    @Field
    private  Map<String, Integer> unReadMessageMap = new HashMap<>();

    private UserPrivateMessageList(String id) {
        this.id = id;
    }

    public static UserPrivateMessageList getInstance(String uid){
        return new UserPrivateMessageList(uid);
    }

    public String addContact(String uid){
        String removeUser = null;
        if(Objects.nonNull(uid) && !contactList.contains(uid)){
            if(contactList.size()>=MAX_CONTACT_RECORD_NUM) {
                removeUser = contactList.remove(0);
                unReadMessageMap.remove(removeUser);
            }
            contactList.add(uid);
        }
        return removeUser;
    }

    public void increaseUnReadMessage(String linkUserId,int num){
        int unReadNum = unReadMessageMap.computeIfAbsent(linkUserId, k -> 0);
        unReadNum += num;
        if (unReadNum<=0) unReadMessageMap.remove(linkUserId);
        else unReadMessageMap.put(linkUserId, unReadNum);
    }

    public void cleanUnReadMessage(String linkUserId){
        unReadMessageMap.remove(linkUserId);
    }

    public List<String> getContactList() {
        return contactList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getUnReadNum(String linkUserId){
        return unReadMessageMap.getOrDefault(linkUserId, 0);
    }
}
