package com.donglai.web.web.dto.reply;

import com.donglai.model.db.entity.common.User;
import com.donglai.model.db.entity.live.Room;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@NoArgsConstructor
public class PlatformRoomListReply {
    public List<String> domains = new ArrayList<>();
    public List<LiveRoomInfo> liveRoomInfo = new ArrayList<>();

    public PlatformRoomListReply(List<String> domains, List<LiveRoomInfo> liveRoomInfo) {
        this.domains = domains;
        this.liveRoomInfo = liveRoomInfo;
    }

    @Data
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class LiveRoomInfo {
        private String nickname;
        private String roomImage;
        private int roomHeat;
        private String roomTitle;
        private String roomUrl;

        public LiveRoomInfo(User user, Room room,String roomUrl){
            this.nickname = user.getNickname();
            this.roomImage = room.getRoomImage();
            this.roomHeat = room.getAudiences().size();
            this.roomTitle = room.getRoomTitle();
            this.roomUrl = roomUrl;
        }
    }
}
