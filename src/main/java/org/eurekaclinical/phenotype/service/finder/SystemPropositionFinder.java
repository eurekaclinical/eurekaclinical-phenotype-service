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

import java.util.List;
import javax.inject.Inject;

import org.protempa.PropositionDefinition;

import org.eurekaclinical.eureka.client.comm.SystemPhenotype;

public class SystemPropositionFinder extends AbstractPropositionFinder<String> {
	private final SystemPropositionRetriever retriever;

	@Inject
	public SystemPropositionFinder(SystemPropositionRetriever inRetriever) {
		super(inRetriever);
		this.retriever = inRetriever;
	}
	
	/**
	 * Finds all of the system elements given by the keys for the given user
	 * 
	 * @param sourceConfigId the ID of the source config to use for the look-up
	 * @param inKeys the keys of the system elements to look up
	 * @param withChildren whether to find the given system elements' children as well
	 * @return a {@link List} of {@link SystemPhenotype}s
         * @throws org.eurekaclinical.phenotype.service.finder.PropositionFindException if error finding proposition
	 */
	public List<PropositionDefinition> findAll(
	        String sourceConfigId, List<String> inKeys, Boolean withChildren) throws PropositionFindException {
		return this.retriever.retrieveAll(sourceConfigId, inKeys, withChildren);
	}

}
