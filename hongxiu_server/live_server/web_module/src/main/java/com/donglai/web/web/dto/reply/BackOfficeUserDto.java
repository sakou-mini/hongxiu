package com.donglai.web.web.dto.reply;

import com.donglai.web.db.backoffice.entity.BackOfficeUser;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class BackOfficeUserDto {
    private String username;
    private String nickname;
    private String avatar = "";

    public BackOfficeUserDto(BackOfficeUser backOfficeUser) {
        this.username = backOfficeUser.getUsername();
        this.nickname = backOfficeUser.getNickname();
        this.avatar = backOfficeUser.getAvatar();
    }

    public static BackOfficeUserDto newInstance(BackOfficeUser backOfficeUser) {
        return new BackOfficeUserDto(backOfficeUser);
    }
}
