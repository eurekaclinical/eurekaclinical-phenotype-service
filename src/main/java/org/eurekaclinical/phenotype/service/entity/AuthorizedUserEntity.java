/*
 * #%L
 * Eureka Common
 * %%
 * Copyright (C) 2012 - 2013 Emory University
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
package org.eurekaclinical.phenotype.service.entity;

import java.util.ArrayList;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import org.eurekaclinical.standardapis.entity.UserEntity;

/**
 * A bean class to hold information about users in the system.
 *
 * @author Andrew Post
 *
 */
@Entity
@Table(name = "users")
public class AuthorizedUserEntity implements UserEntity<AuthorizedRoleEntity> {

	/**
	 * The user's unique identifier.
	 */
	@Id
	@SequenceGenerator(name = "USER_SEQ_GENERATOR", sequenceName = "USER_SEQ",
			allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE,
			generator = "USER_SEQ_GENERATOR")
	private Long id;
        
	/**
	 * The login name of the user.
	 */
	@Column(unique = true, nullable = false)
	private String username;
	
	/**
	 * A list of roles assigned to the user.
	 */
	@ManyToMany(cascade = {CascadeType.REFRESH, CascadeType.MERGE})
	@JoinTable(joinColumns=@JoinColumn(name="user_id"), inverseJoinColumns=@JoinColumn(name="role_id"))
	private List<AuthorizedRoleEntity> roles;

	/**
	 * Create an empty User object.
	 */
	public AuthorizedUserEntity() {
		this.roles = new ArrayList<>();
	}

	/**
	 * Get the user's unique identifier.
	 *
	 * @return A {@link Long} representing the user's unique identifier.
	 */
	@Override
	public Long getId() {
		return this.id;
	}

	/**
	 * Set the user's unique identifier.
	 *
	 * @param inId A {@link Long} representing the user's unique identifier.
	 */
	@Override
	public void setId(final Long inId) {
		this.id = inId;
	}

	/**
	 * Get the user's email address.
	 *
	 * @return A String containing the user's email address.
	 */
	@Override
	public String getUsername() {
		return this.username;
	}

	/**
	 * Set the user's email address.
	 *
	 * @param inUsername A String containing the user's email address.
	 */
	@Override
	public void setUsername(final String inUsername) {
		this.username = inUsername;
	}

	@Override
	public List<AuthorizedRoleEntity> getRoles() {
		return this.roles;
	}

	@Override
	public void setRoles(List<AuthorizedRoleEntity> inRoles) {
		if (inRoles == null) {
			this.roles = new ArrayList<>();
		} else {
			this.roles = inRoles;
		}
	}

	@Override
	public void addRole(AuthorizedRoleEntity role) {
		this.roles.add(role);
	}

	@Override
	public void removeRole(AuthorizedRoleEntity role) {
		this.roles.remove(role);
	}
	
	@Override
	public String toString() {
		return "AuthorizedUserEntity{" + "id=" + id + ", username=" + username + ", roles=" + roles + '}';
	}	
}
