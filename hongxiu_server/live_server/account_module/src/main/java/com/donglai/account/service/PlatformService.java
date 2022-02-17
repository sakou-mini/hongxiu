package com.donglai.account.service;

import com.donglai.model.db.entity.common.User;
import com.donglai.model.dto.Pair;
import com.donglai.protocol.Account;
import com.donglai.protocol.Constant;

public interface PlatformService {
    User register(Account.AccountOfRegisterRequest AccountOfRegisterRequest, Constant.PlatformType platform);

    Pair<Constant.ResultCode, User> loginProcess(Account.AccountOfLoginRequest request,String loginIp);

    String getPlatformDefaultAvatar(Constant.PlatformType platform);
}

