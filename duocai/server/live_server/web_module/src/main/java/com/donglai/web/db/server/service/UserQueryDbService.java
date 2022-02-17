package com.donglai.web.db.server.service;

import com.donglai.model.db.entity.common.User;
import com.donglai.model.db.repository.common.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserQueryDbService {
    @Autowired
    UserRepository userRepository;

    public long countOnlineUserNum(){
        //TODO 这种方式并不准确，需要用redis来统计在线人数,或者需要和gate网关交互 （定时来统计连接数）
        return userRepository.findAll().stream().filter(User::isOnline).count();
    }
}
