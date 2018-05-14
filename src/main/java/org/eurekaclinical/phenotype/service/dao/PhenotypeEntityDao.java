/*
 * #%L
 * Eureka Services
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
package org.eurekaclinical.phenotype.service.dao;

import java.util.List;

//import edu.emory.cci.aiw.cvrg.eureka.common.entity.PhenotypeEntity;
import org.eurekaclinical.standardapis.dao.Dao;

import org.eurekaclinical.phenotype.service.entity.PhenotypeEntity;

/**
 * A data access object interface for working with {@link PhenotypeEntity} objects
 * in the data store.
 *
 * @author hrathod
 */
public interface PhenotypeEntityDao extends Dao<PhenotypeEntity, Long> {
	/**
	 * Gets a user-defined phenotype entity based on the "key" attribute.
	 * @param inUserId The userId to search for in the database.
	 * @param inKey The key to be searched in the database.
	 * @return A proposition if found, null otherwise.
	 */
	public PhenotypeEntity getByUserAndKey(Long inUserId, String inKey);
	
	/**
	 * Gets a system proposition definition that has been loaded previously
	 * into the database.
	 * 
	 * @param inUserId The userId to search for in the database.
	 * @param inKey he key to be searched in the database.
	 * @return a system proposition found in the database, null otherwise.
	 */
	public PhenotypeEntity getUserOrSystemByUserAndKey(Long inUserId, String inKey);

	/**
	 * Gets a list of user-defined phenotype entities for the given user ID.
	 * @param inUserId the unique identifier for the given user.
	 * @return A list of propositions belonging to the given user.
	 */
	public List<PhenotypeEntity> getByUserId(Long inUserId);
	/**
	 * Gets a user-defined phenotype entity based on the given id
	 * 
	 * @param inId The id to search for in the database.
	 * @return A proposition if found, null otherwise.
	 */        
	public PhenotypeEntity getById(Long inId);
}
