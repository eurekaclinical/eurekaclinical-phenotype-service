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
package org.eurekaclinical.phenotype.service.finder;

import org.protempa.PropositionDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractPropositionFinder<K> implements PropositionFinder<K> {

	private static Logger LOGGER = LoggerFactory.getLogger(AbstractPropositionFinder.class);
	private final PropositionRetriever<K> retriever;

	protected AbstractPropositionFinder(PropositionRetriever<K> inRetriever) {
		this.retriever = inRetriever;
	}

	/**
	 * Retrieves a proposition definition from the {@link PropositionRetriever}
	 * specified in this object's constructor.
	 *
	 * @param sourceConfigId the source configuration ID
	 * @param inKey a proposition key.
	 * @return the proposition definition, or <code>null</code> if there is no
	 * proposition definition with the specified key for the specified user.
	 *
	 * @throws PropositionFindException if an error occurred looking for the
	 * proposition definition.
	 */
	@Override
	public PropositionDefinition find(String sourceConfigId, K inKey)
			throws PropositionFindException {
		return this.retriever.retrieve(sourceConfigId, inKey);
	}

}
