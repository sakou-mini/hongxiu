package com.donglaistd.jinli.database.dao.backoffice;

import com.donglaistd.jinli.Constant;
import com.donglaistd.jinli.database.entity.backoffice.ChangeSystemTipMessageRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ChangeSystemTipMessageRecordDaoService {
    @Autowired
    ChangeSystemTipMessageRecordRepository changeSystemTipMessageRecordRepository;

    public ChangeSystemTipMessageRecord save(ChangeSystemTipMessageRecord changeSystemTipMessageRecord){
        return changeSystemTipMessageRecordRepository.save(changeSystemTipMessageRecord);
    }

    public Page<ChangeSystemTipMessageRecord> queryChangeSystemTipMessageRecordByPlatform(int page, int size, Constant.PlatformType platformType){
        if(page<0) page = 0;
        Pageable pageable = PageRequest.of(page,size);
        return changeSystemTipMessageRecordRepository.findAllByPlatformIsOrderByRecordTimeDesc(platformType,pageable);
    }
}
