package com.donglai.web.service.impl;

import com.donglai.model.db.entity.common.Keyword;
import com.donglai.model.db.service.common.KeywordService;
import com.donglai.web.db.backoffice.entity.BackOfficeUser;
import com.donglai.web.db.backoffice.service.BackOfficeUserService;
import com.donglai.web.db.server.service.KeywordDbService;
import com.donglai.web.response.*;
import com.donglai.web.service.KeywordBackService;
import com.donglai.web.util.ConvertUtils;
import com.donglai.web.web.dto.request.KeywordFindListRequest;
import com.donglai.web.web.dto.request.KeywordInsertRequest;
import com.donglai.web.web.dto.request.KeywordUpdateStatusRequest;
import com.donglai.web.web.vo.KeywordVO;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;

/**
 * @author Moon
 * @date 2021-12-28 14:10
 */
@Service
public class KeywordServiceImpl implements KeywordBackService {
    @Autowired
    private KeywordService keywordService;
    @Autowired
    private KeywordDbService keywordDbService;
    @Autowired
    private BackOfficeUserService backOfficeUserService;

    @Override
    public RestResponse insert(KeywordInsertRequest request) {
        if(StringUtils.isEmpty(request.getWord())){
            return new ErrorResponse(GlobalResponseCode.PARAM_ERROR);
        }
        BackOfficeUser principal =(BackOfficeUser) SecurityUtils.getSubject().getPrincipal();


        Keyword keyword = new Keyword();
        keyword.setCreatedId(principal.getId());
        keyword.setUpdatedTime(System.currentTimeMillis());
        keyword.setCreatedTime(System.currentTimeMillis());
        keyword.setStatus(true);
        keyword.setWord(request.getWord());
        return new SuccessResponse().withData(keywordService.save(keyword));
    }

    @Override
    public RestResponse delete(KeywordUpdateStatusRequest request) {
        if (CollectionUtils.isEmpty(request.getIds())) {
            return new ErrorResponse(GlobalResponseCode.PARAM_ERROR);
        }
        return new SuccessResponse().withData(keywordService.deleteByIdIn(request.getIds()));

    }

    @Override
    public RestResponse updateStatus(KeywordUpdateStatusRequest request) {
        if (CollectionUtils.isEmpty(request.getIds()) || Objects.isNull(request.getStatus())) {
            return new ErrorResponse(GlobalResponseCode.PARAM_ERROR);
        }
        List<Keyword> byIdIn = keywordService.findByIdIn(request.getIds());
        byIdIn.forEach(v -> v.setStatus(request.getStatus()));
        return new SuccessResponse().withData(keywordService.saveAll(byIdIn));

    }

    @Override
    public PageInfo<KeywordVO> findList(KeywordFindListRequest request) {
        PageInfo<Keyword> page = keywordDbService.conditionGetKeyword(request);

        List<KeywordVO> vos = ConvertUtils.keywordListToKeywordVOList(page.getContent());

        for (KeywordVO vo : vos) {
            if(StringUtils.isNotEmpty(vo.getCreatedId())){
                BackOfficeUser byId = backOfficeUserService.findById(vo.getCreatedId());
                if(Objects.nonNull(byId)){
                    vo.setCreatedId(byId.getNickname());
                }
            }
        }
        return new PageInfo<>(page.getPageNum(), page.getPageSize(), page.getTotal(), vos);
    }
}
