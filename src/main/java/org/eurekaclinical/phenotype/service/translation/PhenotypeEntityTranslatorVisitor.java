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

import org.eurekaclinical.phenotype.service.entity.CategoryEntity;
import org.eurekaclinical.phenotype.service.entity.FrequencyEntity;
import org.eurekaclinical.phenotype.service.entity.SequenceEntity;
import org.eurekaclinical.phenotype.service.entity.SystemProposition;
import org.eurekaclinical.phenotype.service.entity.ValueThresholdGroupEntity;
import org.eurekaclinical.phenotype.service.entity.PhenotypeEntityVisitor;

import org.eurekaclinical.eureka.client.comm.Phenotype;
//import org.eurekaclinical.phenotype.service.entity.CategoryEntity;
//import org.eurekaclinical.phenotype.service.entity.FrequencyEntity;
//import org.eurekaclinical.phenotype.service.entity.SequenceEntity;
//import org.eurekaclinical.phenotype.service.entity.SystemProposition;
//import org.eurekaclinical.phenotype.service.entity.ValueThresholdGroupEntity;
//import org.eurekaclinical.phenotype.service.entity.PhenotypeEntityVisitor;

public final class PhenotypeEntityTranslatorVisitor implements
		PhenotypeEntityVisitor {

	private final SystemPropositionTranslator systemPropositionTranslator;
	private final SequenceTranslator sequenceTranslator;
	private final CategorizationTranslator categorizationTranslator;
	private final FrequencyTranslator frequencyTranslator;
	private final ValueThresholdsTranslator valueThresholdsTranslator;
	
	private Phenotype phenotype;

	@Inject
	public PhenotypeEntityTranslatorVisitor(
			SystemPropositionTranslator inSystemPropositionTranslator,
			SequenceTranslator inSequenceTranslator,
			CategorizationTranslator inCategorizationTranslator,
			FrequencyTranslator inFrequencyTranslator,
			ValueThresholdsTranslator inValueThresholdsTranslator) {
		this.systemPropositionTranslator = inSystemPropositionTranslator;
		this.categorizationTranslator = inCategorizationTranslator;
		this.sequenceTranslator = inSequenceTranslator;
		this.frequencyTranslator = inFrequencyTranslator;
		this.valueThresholdsTranslator = inValueThresholdsTranslator;
	}

	public Phenotype getPhenotype() {
		return phenotype;
	}

	@Override
	public void visit(SystemProposition entity) {
		phenotype = this.systemPropositionTranslator
				.translateFromProposition(entity);
	}

	@Override
	public void visit(CategoryEntity entity) {
		phenotype = this.categorizationTranslator
				.translateFromProposition(entity);
	}

	@Override
	public void visit(SequenceEntity entity) {
		phenotype = this.sequenceTranslator
				.translateFromProposition(entity);
	}

	@Override
	public void visit(FrequencyEntity entity) {
		phenotype = this.frequencyTranslator
				.translateFromProposition(entity);
	}

	@Override
	public void visit(ValueThresholdGroupEntity entity) {
		phenotype = this.valueThresholdsTranslator
				.translateFromProposition(entity);
	}
}
