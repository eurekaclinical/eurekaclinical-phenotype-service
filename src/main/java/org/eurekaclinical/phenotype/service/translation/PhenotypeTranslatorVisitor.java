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

import com.google.inject.Inject;

import org.eurekaclinical.phenotype.service.entity.PhenotypeEntity;

import org.eurekaclinical.eureka.client.comm.Category;
import org.eurekaclinical.eureka.client.comm.Frequency;
import org.eurekaclinical.eureka.client.comm.Sequence;
import org.eurekaclinical.eureka.client.comm.SystemPhenotype;
import org.eurekaclinical.eureka.client.comm.ValueThresholds;
//import edu.emory.cci.aiw.cvrg.eureka.common.entity.PhenotypeEntity;
import org.eurekaclinical.eureka.client.comm.exception.PhenotypeHandlingException;
import org.eurekaclinical.eureka.client.comm.PhenotypeVisitor;

public final class PhenotypeTranslatorVisitor implements PhenotypeVisitor {

	private final SystemPropositionTranslator systemPropositionTranslator;
	private final SequenceTranslator sequenceTranslator;
	private final CategorizationTranslator categorizationTranslator;
	private final FrequencyTranslator frequencyTranslator;
	private final ValueThresholdsTranslator valueThresholdsTranslator;

	private PhenotypeEntity phenotypeEntity;

	@Inject
	public PhenotypeTranslatorVisitor(
	        SystemPropositionTranslator inSystemPropositionTranslator,
	        SequenceTranslator inSequenceTranslator,
	        CategorizationTranslator inCategorizationTranslator,
	        FrequencyTranslator inFrequencyTranslator,
	        ValueThresholdsTranslator inValueThresholdsTranslator) {
		this.systemPropositionTranslator = inSystemPropositionTranslator;
		this.sequenceTranslator = inSequenceTranslator;
		this.categorizationTranslator = inCategorizationTranslator;
		this.frequencyTranslator = inFrequencyTranslator;
		this.valueThresholdsTranslator = inValueThresholdsTranslator;
	}

	public PhenotypeEntity getPhenotypeEntity() {
		return phenotypeEntity;
	}

	@Override
	public void visit(SystemPhenotype systemElement)
	        throws PhenotypeHandlingException {
		phenotypeEntity = this.systemPropositionTranslator
		        .translateFromPhenotype(systemElement);


	}

	@Override
	public void visit(Category categoricalPhenotype)
	        throws PhenotypeHandlingException {
		phenotypeEntity = this.categorizationTranslator
		        .translateFromPhenotype(categoricalPhenotype);
	}

	@Override
	public void visit(Sequence sequence) throws PhenotypeHandlingException {
		phenotypeEntity = this.sequenceTranslator.translateFromPhenotype(sequence);
	}

	@Override
	public void visit(Frequency frequency) throws PhenotypeHandlingException {
		phenotypeEntity = this.frequencyTranslator.translateFromPhenotype(frequency);
	}

	@Override
	public void visit(ValueThresholds thresholds)
	        throws PhenotypeHandlingException {
		phenotypeEntity = this.valueThresholdsTranslator.translateFromPhenotype(thresholds);
	}

}
