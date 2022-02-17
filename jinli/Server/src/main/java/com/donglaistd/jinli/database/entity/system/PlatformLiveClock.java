package com.donglaistd.jinli.database.entity.system;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class PlatformLiveClock {
    @Id
    private String id;  //this id equals PlatformTypeEnum Code

}
