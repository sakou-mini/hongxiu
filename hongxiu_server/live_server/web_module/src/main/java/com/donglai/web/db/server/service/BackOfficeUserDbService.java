package com.donglai.web.db.server.service;

import com.donglai.web.db.backoffice.entity.BackOfficeUser;
import com.donglai.web.db.backoffice.service.BackOfficeUserService;
import com.donglai.web.response.PageInfo;
import com.donglai.web.web.dto.request.BackOfficeUserFindListRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Moon
 * @date 2021-12-29 10:51
 */
@Service
public class BackOfficeUserDbService {
    @Autowired
    private BackOfficeUserService backOfficeUserService;

    public PageInfo<BackOfficeUser> conditionGetKeyword(BackOfficeUserFindListRequest request) {

        return backOfficeUserService.pageQuery(request.getRoleGroup(), request.getEnabled(), request.getPage(), request.getSize());
    }
}
