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
package org.eurekaclinical.phenotype.service.translation;

import org.eurekaclinical.eureka.client.comm.Phenotype;

import org.eurekaclinical.eureka.client.comm.exception.PhenotypeHandlingException;

import org.eurekaclinical.phenotype.service.entity.PhenotypeEntity;

/**
 * Translates a UI phenotype into a proposition as understood by the service
 * layer data model.
 * 
 * @param <P>
 *            The phenotype type to translate from, a subclass of
 *            {@link Phenotype}..
 * @param <PE>
 *            The proposition type to translate to, an implementation of
 *            {@link PhenotypeEntity}.
 */
public interface PropositionTranslator<P extends Phenotype, PE extends PhenotypeEntity> {

	/**
	 * Translates the given phenotype to a proposition understood by the
	 * services layer data model. The inverse of {@link #translateFromProposition(PhenotypeEntity)}
	 * .
	 * 
	 * @param phenotype
	 *            the phenotype to translate from
	 * @return A {@link PhenotypeEntity} equivalent to the phenotype.
	 * @throws PhenotypeHandlingException if an error occurred retrieving
	 * proposition definitions.
	 */
	PE translateFromPhenotype(P phenotype) throws PhenotypeHandlingException;

	/**
	 * Translates the given proposition entity into a phenotype understood by
	 * the webapp layer. The inverse of {@link #translateFromPhenotype(Phenotype)}.
	 * 
	 * @param proposition
	 *            the proposition to translate from
	 * @return A {@link Phenotype} equivalent to the proposition.
	 */
	P translateFromProposition(PE proposition);
}
