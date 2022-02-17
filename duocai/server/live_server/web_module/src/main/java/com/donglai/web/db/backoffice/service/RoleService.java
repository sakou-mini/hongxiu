package com.donglai.web.db.backoffice.service;

import com.donglai.web.db.backoffice.entity.Role;
import com.donglai.web.db.backoffice.repository.RoleRepository;
import com.donglai.web.db.server.service.CommonQueryService;
import com.donglai.web.response.PageInfo;
import com.donglai.web.web.dto.request.FindConditionListRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

import static com.donglai.web.constant.BackOfficeRole.ROLE_ADMIN;
import static com.donglai.web.constant.BackOfficeRole.ROLE_DUOCAI_PLATFORM;
import static com.donglai.web.constant.DataBaseFiledConstant.*;

@Service
public class RoleService {
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    MongoTemplate mongoTemplate;

    public Role save(Role role){
        return roleRepository.save(role);
    }

    public List<Role> saveAll(List<Role> roles) {
        return roleRepository.saveAll(roles);
    }

    public Role findByRoleName(String roleName){
        return roleRepository.findByName(roleName);
    }

    public Role findByRoleId(String roleId){
        return roleRepository.findById(roleId).orElse(null);
    }

    public List<Role> findByRoleIdIn(List<String> ids) {
        return roleRepository.findByIdIn(ids);
    }

    public void deleteAll() {
        roleRepository.deleteAll();
    }

    public void deleteByIdIn(List<String> ids) {
        roleRepository.deleteByIdIn(ids);
    }

    public PageInfo<Role> findConditionList(FindConditionListRequest request) {
        Criteria criteria = Criteria.where(ROLE_NAME).nin(ROLE_ADMIN.name(), ROLE_DUOCAI_PLATFORM.name());
        if (Objects.nonNull(request.getStatus())) {
            criteria.and(ROLE_STATUS).is(request.getStatus());
        }
        CommonQueryService.getCriteriaByTimes(request.getStartTime(),request.getEndTime(),criteria, ROLE_CREATE_TIME);
        Pageable pageable = PageRequest.of(request.getPage() - 1, request.getSize());
        Query query = Query.query(criteria);
        long count = mongoTemplate.count(query, Role.class);
        List<Role> reports = mongoTemplate.find(query.with(pageable).with(Sort.by(Sort.Direction.DESC, ROLE_CREATE_TIME)), Role.class);

        return new PageInfo<>(pageable, reports, count);
    }

    public List<Role> getAllUsableRoles(){
        return roleRepository.findAllByEnabledOrderByCreatedAsc(true);
    }
}
