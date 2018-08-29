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

//import org.eurekaclinical.phenotype.service.entity.FrequencyType;
import java.util.List;
import org.eurekaclinical.standardapis.dao.Dao;
import org.protempa.CompoundLowLevelAbstractionDefinition.ValueDefinitionMatchOperator;

import org.eurekaclinical.phenotype.client.comm.FrequencyType;

/**
 *
 */
public interface FrequencyTypeDao  extends
		Dao<FrequencyType, Long> {
	/**
	 * Gets a value definition match operator based on the name attribute.
	 * @param inName the name to search for in the database
	 * @return a {@link ValueDefinitionMatchOperator} with the given name if
	 * found, null otherwise
	 */
	FrequencyType getByName(String inName);
	
	FrequencyType getDefault();
	
	List<FrequencyType> getAllAsc();
	
	
}
