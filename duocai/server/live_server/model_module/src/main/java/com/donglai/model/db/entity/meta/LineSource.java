package com.donglai.model.db.entity.meta;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
public class LineSource {
    public int id;
    public String handlerClass;

    public LineSource(int id) {
        this.id = id;
    }
}
