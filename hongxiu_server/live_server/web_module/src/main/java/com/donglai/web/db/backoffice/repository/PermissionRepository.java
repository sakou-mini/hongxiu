package com.donglai.web.db.backoffice.repository;

import com.donglai.web.db.backoffice.entity.Permission;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PermissionRepository extends MongoRepository<Permission,String> {
}
