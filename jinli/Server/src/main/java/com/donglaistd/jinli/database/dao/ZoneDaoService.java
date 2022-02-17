package com.donglaistd.jinli.database.dao;

import com.donglaistd.jinli.database.entity.zone.Zone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ZoneDaoService {
    @Autowired
    ZoneRepository zoneRepository;

    public Zone save(Zone zone) {
        return zoneRepository.save(zone);
    }

    public Zone findOrCreateZoneByUserId(String userId){
        Zone zone = zoneRepository.findByUserId(userId);
        if(zone == null){
            zone = Zone.getInstance(userId);
            zoneRepository.save(zone);
        }
        return zone;
    }

    public Zone findByUserId(String userId){
        return zoneRepository.findByUserId(userId);
    }
}
