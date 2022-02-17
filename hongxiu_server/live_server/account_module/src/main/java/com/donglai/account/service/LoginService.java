package com.donglai.account.service;

import com.donglai.model.db.entity.common.User;
import com.donglai.model.dto.Pair;
import com.donglai.protocol.Account;
import com.donglai.protocol.Constant;

/**
 * @author Moon
 * @date 2021-11-18 14:42
 */
public interface LoginService {
    /**
     * 登录借口
     *
     * @param request 参数
     * @return 返回是否登录成功
     */
    Pair<Constant.ResultCode, User> login(Account.AccountOfLoginRequest request,String loginIp);
}
