package com.donglaistd.jinli.util;

import com.donglaistd.jinli.config.SaveEventListener;
import com.donglaistd.jinli.database.dao.BackOfficeUserDaoService;
import com.donglaistd.jinli.database.dao.LiveUserDaoService;
import com.donglaistd.jinli.database.dao.RoomDaoService;
import com.donglaistd.jinli.database.dao.UserDaoService;
import com.donglaistd.jinli.database.entity.LiveUser;
import com.donglaistd.jinli.database.entity.Room;
import com.donglaistd.jinli.database.entity.User;
import com.donglaistd.jinli.database.entity.backoffice.BackOfficeUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IdGeneratedProcess {
    private static final String SUFFIX_ID = "_id";

    private static final String SUFFIX_NAME = "_name";
    @Autowired
    UserDaoService userDaoService;
    @Autowired
    LiveUserDaoService liveUserDaoService;
    @Autowired
    RoomDaoService roomDaoService;
    @Autowired
    BackOfficeUserDaoService backOfficeUserDaoService;

    @Autowired
    SaveEventListener saveEventListener;

    public String generatedId(String collection,int length){
        return StringNumberUtils.generateGameId(saveEventListener.getNextId(collection + SUFFIX_ID), length);
    }

    public String generatedName(String collection){
        return StringUtils.generateTouristName(saveEventListener.getNextId(collection + SUFFIX_NAME));
    }

    public void initCollId(){
        saveEventListener.initCollId(User.class.getSimpleName()+ SUFFIX_ID, (int) userDaoService.count());
        saveEventListener.initCollId(User.class.getSimpleName()+"displayName"+ SUFFIX_ID, (int) userDaoService.count());
        saveEventListener.initCollId(LiveUser.class.getSimpleName()+ SUFFIX_ID, (int) liveUserDaoService.countLiveUser());
        saveEventListener.initCollId(Room.class.getSimpleName()+ SUFFIX_ID, (int) roomDaoService.count());
        saveEventListener.initCollId(BackOfficeUser.class.getSimpleName()+ SUFFIX_ID, backOfficeUserDaoService.findAll().size());

        //namecolumn
        saveEventListener.initCollId(User.class.getSimpleName()+ SUFFIX_NAME, (int) userDaoService.count());
    }
}
