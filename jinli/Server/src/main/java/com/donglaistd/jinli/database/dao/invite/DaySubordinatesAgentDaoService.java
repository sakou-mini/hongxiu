package com.donglaistd.jinli.database.dao.invite;

import com.donglaistd.jinli.database.entity.invite.DaySubordinatesAgent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public class DaySubordinatesAgentDaoService {

    @Autowired
    DaySubordinatesAgentRepository daySubordinatesAgentRepository;

    public void saveAll(List<DaySubordinatesAgent> daySubordinatesAgentList){
        daySubordinatesAgentRepository.saveAll(daySubordinatesAgentList);
    }

    public List<DaySubordinatesAgent> findByUserIds(Collection<String> ids){
        return daySubordinatesAgentRepository.findAllByUserIdIn(ids);
    }
}
