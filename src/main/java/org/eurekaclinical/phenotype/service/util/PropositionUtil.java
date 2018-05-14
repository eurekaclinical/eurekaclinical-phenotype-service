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
package org.eurekaclinical.phenotype.service.util;

import java.util.ArrayList;
import java.util.List;

import org.arp.javautil.arrays.Arrays;
import org.protempa.PropertyDefinition;
import org.protempa.PropositionDefinition;

import org.eurekaclinical.eureka.client.comm.SystemPhenotype;

import org.eurekaclinical.phenotype.service.entity.SystemProposition;
import org.eurekaclinical.phenotype.service.entity.PhenotypeEntity;
//import edu.emory.cci.aiw.cvrg.eureka.common.entity.PhenotypeEntity;
//import edu.emory.cci.aiw.cvrg.eureka.common.entity.SystemProposition;
import org.eurekaclinical.phenotype.service.finder.PropositionFindException;
import org.eurekaclinical.phenotype.service.finder.SystemPropositionFinder;

/**
 * Provides common utility functions operating on {@link PhenotypeEntity}s.
 */
public final class PropositionUtil {

	private PropositionUtil() {
		// do not allow instantiation.
	}

	public static SystemProposition toSystemProposition(
	        PropositionDefinition inDefinition, Long inUserId) {
		if (inDefinition == null) {
			throw new IllegalArgumentException("inDefinition cannot be null");
		}
		SystemProposition sysProp = new SystemProposition();
		sysProp.setKey(inDefinition.getId());
		sysProp.setInSystem(true);
		sysProp.setDisplayName(inDefinition.getDisplayName());
		sysProp.setDescription(inDefinition.getAbbreviatedDisplayName());
		sysProp.setUserId(inUserId);
		PropositionDefinitionTypeVisitor propDefTypeVisitor = new PropositionDefinitionTypeVisitor();
		inDefinition.accept(propDefTypeVisitor);
		sysProp.setSystemType(propDefTypeVisitor.getSystemType());
		return sysProp;
	}

	/**
	 * Wraps a proposition definition into a proposition wrapper.
	 */
	public static SystemPhenotype toSystemPhenotype(
			String sourceConfigId,
	        PropositionDefinition inDefinition, boolean summarize,
	        SystemPropositionFinder inPropositionFinder)
	        throws PropositionFindException {
		if (inDefinition == null) {
			throw new IllegalArgumentException("inDefinition cannot be null");
		}
		SystemPhenotype systemPhenotype = new SystemPhenotype();
		systemPhenotype.setKey(inDefinition.getId());
		systemPhenotype.setInSystem(true);
		systemPhenotype.setInternalNode(inDefinition.getChildren().length > 0);
		systemPhenotype.setDescription(inDefinition.getAbbreviatedDisplayName());
		systemPhenotype.setDisplayName(inDefinition.getDisplayName());
		systemPhenotype.setSummarized(summarize);
		PropositionDefinitionTypeVisitor propDefTypeVisitor = new PropositionDefinitionTypeVisitor();
		inDefinition.accept(propDefTypeVisitor);
		systemPhenotype.setSystemType(propDefTypeVisitor.getSystemType());
		String[] inDefChildren = inDefinition.getChildren();
		systemPhenotype.setParent(inDefChildren.length > 0);

		List<String> properties = new ArrayList<>();
		for (PropertyDefinition propertyDef : inDefinition
				.getPropertyDefinitions()) {
			properties.add(propertyDef.getId());
		}
		systemPhenotype.setProperties(properties);

		if (!summarize) {
			List<SystemPhenotype> children = new ArrayList<>();
			List<PropositionDefinition> pds = inPropositionFinder.findAll(
					sourceConfigId,
			        Arrays.<String> asList(inDefChildren),
			        Boolean.FALSE);
			for (PropositionDefinition pd : pds) {
				children.add(toSystemPhenotype(sourceConfigId, pd, true,
				        inPropositionFinder));
			}
			systemPhenotype.setChildren(children);

		}

		return systemPhenotype;
	}
}
