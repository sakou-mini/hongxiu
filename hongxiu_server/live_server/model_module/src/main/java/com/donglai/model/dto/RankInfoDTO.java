package com.donglai.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RankInfoDTO {
    private String id;
    private long score;

    public static RankInfoDTO newInstance(String id, long score) {
        return new RankInfoDTO(id, score);
    }
}
