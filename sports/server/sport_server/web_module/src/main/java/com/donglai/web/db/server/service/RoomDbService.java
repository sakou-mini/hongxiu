package com.donglai.web.db.server.service;

import com.donglai.common.util.StringUtils;
import com.donglai.model.db.entity.common.User;
import com.donglai.model.db.entity.live.Room;
import com.donglai.model.db.service.common.UserService;
import com.donglai.model.db.service.live.RoomService;
import com.donglai.web.response.PageInfo;
import com.donglai.web.util.ComparatorUtil;
import com.donglai.web.web.dto.request.LiveRoomRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class RoomDbService {
    @Autowired
    RoomService roomService;
    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    UserService userService;

    public PageInfo<Room> findLiveRoomByRequest(LiveRoomRequest request){
        List<Predicate<Room>> predicates = new ArrayList<>(3);
        if (!StringUtils.isNullOrBlank(request.getLiveUserId())) {
            predicates.add(room -> room.getLiveUserId().equals(request.getLiveUserId()));
        }
        if (!StringUtils.isNullOrBlank(request.getNickname())){
            List<String> userIds = userService.findByNickname(request.getNickname()).stream().map(User::getId).collect(Collectors.toList());
            predicates.add(room -> userIds.contains(room.getUserId()));
        }
        if (!StringUtils.isNullOrBlank(request.getRoomId())) {
            predicates.add(room -> room.getId().equals(request.getRoomId()));
        }
        Stream<Room> roomStream = roomService.getAllLiveRoom().stream().map(id -> roomService.findById(id)).filter(room -> !room.isClose());
        for (Predicate<Room> predicate : predicates) {
            roomStream = roomStream.filter(predicate);
        }
        List<Room> rooms = roomStream.sorted(ComparatorUtil.getRoomComparator()).collect(Collectors.toList());
        return new PageInfo<>(request.getPage(), request.getSize(), rooms.size(),pageBySubList(rooms, request.getPage(), request.getSize()));
    }

    private List<Room> pageBySubList(List<Room> list, int currentPage,int pageSize) {
        return list.stream().skip((currentPage - 1) * pageSize).limit(pageSize).collect(Collectors.toList());
    }
}
