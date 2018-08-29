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
package org.eurekaclinical.phenotype.service.conversion;

import org.eurekaclinical.phenotype.service.entity.PhenotypeEntity;
import org.protempa.PropositionDefinition;

import java.util.List;

/**
 * Converts Eureka! database entities into equivalent Protempa proposition
 * definitions.
 *
 * @param <E> The type of {@link PhenotypeEntity} to convert.
 */
interface PropositionDefinitionConverter<E extends PhenotypeEntity,
		P extends PropositionDefinition> {

	/**
	 * Converts a Eureka database entity into an equivalent list of Protempa
	 * proposition definitions.
	 *
	 * @param entity The database entity to convert
	 * @return a list of {@link PropositionDefinition}s
	 */
	List<PropositionDefinition> convert(E entity);

	/**
	 * Retrieves the primary proposition created by a call to
	 * {@link #convert(org.eurekaclinical.phenotype.service.entity.PhenotypeEntity)}.
	 * This is mostly for converters invoking each other and needing only
	 * the final highest level (ie, primary) proposition that was created.
	 *
	 * @return a {@link PropositionDefinition}.
	 */
	P getPrimaryPropositionDefinition();
	
	String getPrimaryPropositionId();
}
