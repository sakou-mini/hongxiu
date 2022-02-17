package com.donglai.model.db.entity.meta;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class LineSource {
    public int id;
    public String handlerClass;
}
