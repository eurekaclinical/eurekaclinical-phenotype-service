package org.eurekaclinical.phenotype.service.resource;

/*-
 * #%L
 * eurekaclinical-phenotype-service
 * %%
 * Copyright (C) 2018 Emory University
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import com.google.inject.persist.Transactional;
import org.eurekaclinical.common.comm.User;
import org.eurekaclinical.common.resource.AbstractUserResource;

import org.eurekaclinical.phenotype.service.dao.RoleDao;
import org.eurekaclinical.phenotype.service.dao.AuthorizedUserDao;
import org.eurekaclinical.phenotype.service.entity.AuthorizedRoleEntity;
import org.eurekaclinical.phenotype.service.entity.AuthorizedUserEntity;
import org.eurekaclinical.phenotype.service.entity.UserEntity;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Path;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Transactional
@Path("/protected/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource extends AbstractUserResource<User, AuthorizedUserEntity, AuthorizedRoleEntity> {

	private final RoleDao roleDao;
	
	/**
	 * Create a UserResource object with a User DAO and a Role DAO.
	 *
	 * @param inUserDao DAO used to access {@link UserEntity} related functionality.
	 * @param inRoleDao
	 */

    @Inject
    public UserResource(AuthorizedUserDao inUserDao, RoleDao inRoleDao) {
        super(inUserDao);
        this.roleDao = inRoleDao;
    }

    @Override
    protected User toComm(AuthorizedUserEntity userEntity, HttpServletRequest req) {
        User user = new User();
        user.setId(userEntity.getId());
        user.setUsername(userEntity.getUsername());
        List<Long> roles = new ArrayList<>();
        for (AuthorizedRoleEntity roleEntity : userEntity.getRoles()) {
            roles.add(roleEntity.getId());
        }
        user.setRoles(roles);
        return user;
    }

    @Override
    protected AuthorizedUserEntity toEntity(User user) {
        List<AuthorizedRoleEntity> roleEntities = this.roleDao.getAll();
        AuthorizedUserEntity userEntity = new AuthorizedUserEntity();
        userEntity.setId(user.getId());
        userEntity.setUsername(user.getUsername());
        List<AuthorizedRoleEntity> userRoleEntities = new ArrayList<>();
        for (Long roleId : user.getRoles()) {
            for (AuthorizedRoleEntity roleEntity : roleEntities) {
                if (roleEntity.getId().equals(roleId)) {
                    userRoleEntities.add(roleEntity);
                }
            }
        }
        userEntity.setRoles(userRoleEntities);
        return userEntity;
    }
}
