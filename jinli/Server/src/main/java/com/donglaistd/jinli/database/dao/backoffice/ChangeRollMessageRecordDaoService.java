package com.donglaistd.jinli.database.dao.backoffice;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.entity.backoffice.ChangeRollMessageRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ChangeRollMessageRecordDaoService {
    @Autowired
    ChangeRollMessageRecordRepository changeRollMessageRecordRepository;

    public ChangeRollMessageRecord save(ChangeRollMessageRecord changeRollMessageRecord){
        return changeRollMessageRecordRepository.save(changeRollMessageRecord);
    }

    public Page<ChangeRollMessageRecord> queryChangeRollMessageRecordByPlatform(int page, int size, Constant.PlatformType platformType){
        if(page<0) page = 0;
        Pageable pageable = PageRequest.of(page,size);
        return changeRollMessageRecordRepository.findAllByPlatformOrderByRecordTimeDesc(platformType, pageable);
    }
}
