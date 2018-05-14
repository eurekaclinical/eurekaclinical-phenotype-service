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

import org.eurekaclinical.eureka.client.comm.Category;
import org.eurekaclinical.eureka.client.comm.Phenotype;
import org.eurekaclinical.eureka.client.comm.Frequency;
import org.eurekaclinical.eureka.client.comm.Sequence;
import org.eurekaclinical.eureka.client.comm.SystemPhenotype;
import org.eurekaclinical.eureka.client.comm.ValueThresholds;
import org.eurekaclinical.phenotype.service.entity.CategoryEntity;
import org.eurekaclinical.phenotype.service.entity.PhenotypeEntity;
import org.eurekaclinical.phenotype.service.entity.FrequencyEntity;
import org.eurekaclinical.phenotype.service.entity.SequenceEntity;
import org.eurekaclinical.phenotype.service.entity.SystemProposition;
import org.eurekaclinical.phenotype.service.entity.ValueThresholdGroupEntity;
import org.eurekaclinical.phenotype.service.entity.PhenotypeEntityVisitor;

public final class SummarizingPhenotypeEntityTranslatorVisitor implements
		PhenotypeEntityVisitor {

	private Phenotype phenotype;

	public SummarizingPhenotypeEntityTranslatorVisitor() {
	}

	public Phenotype getPhenotype() {
		return phenotype;
	}

	@Override
	public void visit(SystemProposition entity) {
		this.phenotype = new SystemPhenotype();
		populate(entity);
	}

	@Override
	public void visit(CategoryEntity entity) {
		this.phenotype = new Category();
		populate(entity);
	}

	@Override
	public void visit(SequenceEntity entity) {
		this.phenotype = new Sequence();
		populate(entity);
	}

	@Override
	public void visit(FrequencyEntity entity) {
		this.phenotype = new Frequency();
		populate(entity);
	}

	@Override
	public void visit(ValueThresholdGroupEntity entity) {
		this.phenotype = new ValueThresholds();
		populate(entity);
	}

	private void populate(PhenotypeEntity phenotypeEntity) {
		this.phenotype.setSummarized(true);
		PropositionTranslatorUtil.populateCommonPhenotypeFields(phenotype, 
				phenotypeEntity);
	}
	
}
