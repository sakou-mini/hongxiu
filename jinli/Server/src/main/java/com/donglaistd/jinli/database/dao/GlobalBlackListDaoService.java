package com.donglaistd.jinli.database.dao;

import com.donglaistd.jinli.database.entity.GlobalBlackList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GlobalBlackListDaoService {
    @Autowired
    private GlobalBlackListRepository globalBlackListRepository;

    public Map<String, GlobalBlackList> finAllGlobalBlackList() {
        return globalBlackListRepository.findAll().stream().collect(Collectors.toMap(GlobalBlackList::getUserId, value->value));
    }

    public List<GlobalBlackList> saveAll(Collection<GlobalBlackList> blackLists){
        return globalBlackListRepository.saveAll(blackLists);
    }

    public GlobalBlackList save(GlobalBlackList blackList){
        return globalBlackListRepository.save(blackList);
    }

    public boolean isGlobalMute(String userId){
        Optional<GlobalBlackList> globalBlackList = globalBlackListRepository.findById(userId);
        return globalBlackList.map(blackList -> blackList.getMuteProperty().isMute()).orElse(false);
    }

    public GlobalBlackList finByUserId(String uid){
        return globalBlackListRepository.findById(uid).orElse(null);
    }

    public void deleteByUserId(String userId){
        globalBlackListRepository.deleteById(userId);
    }
}
