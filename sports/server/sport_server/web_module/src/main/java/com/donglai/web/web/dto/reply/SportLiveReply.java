package com.donglai.web.web.dto.reply;

import com.donglai.model.db.entity.common.User;
import com.donglai.model.db.entity.live.Room;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class SportLiveReply {
    public List<String> domains;
    public List<SportLiveInfo> liveRaceInfo;

    @Data
    public static class SportLiveInfo{
       private String liveUserId;
       private String nickName;
       private String avatarUrl;
       private String roomTitle;
       private boolean live;
       private String roomUrl;
       private String eventId;

       public SportLiveInfo(User user, Room room, boolean live, String eventId) {
           this.liveUserId = user.getLiveUserId();
           this.nickName = user.getNickname();
           this.avatarUrl = user.getAvatarUrl();
           this.roomTitle = room.getRoomTitle();
           this.live = live;
           this.eventId = eventId;
       }
   }
}
