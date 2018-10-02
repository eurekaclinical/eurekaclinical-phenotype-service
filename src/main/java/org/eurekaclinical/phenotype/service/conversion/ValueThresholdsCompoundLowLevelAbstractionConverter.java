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

import org.eurekaclinical.phenotype.service.entity.ExtendedPhenotype;
import org.eurekaclinical.phenotype.service.entity.ValueThresholdEntity;
import org.eurekaclinical.phenotype.service.entity.ValueThresholdGroupEntity;
import static org.eurekaclinical.phenotype.service.conversion.ConversionUtil.extractContextDefinition;
import java.util.ArrayList;
import java.util.List;
import org.protempa.CompoundLowLevelAbstractionDefinition;
import org.protempa.ContextDefinition;
import org.protempa.LowLevelAbstractionDefinition;
import org.protempa.PropositionDefinition;
import org.protempa.SimpleGapFunction;
import org.protempa.SlidingWindowWidthMode;
import org.protempa.ValueClassification;

public final class ValueThresholdsCompoundLowLevelAbstractionConverter
		extends AbstractValueThresholdGroupEntityConverter
		implements
		PropositionDefinitionConverter<ValueThresholdGroupEntity, CompoundLowLevelAbstractionDefinition> {

	private PropositionDefinitionConverterVisitor converterVisitor;
	private CompoundLowLevelAbstractionDefinition primary;
	private String primaryPropId;

	public void setConverterVisitor(PropositionDefinitionConverterVisitor inConverterVisitor) {
		converterVisitor = inConverterVisitor;
	}

	@Override
	public CompoundLowLevelAbstractionDefinition getPrimaryPropositionDefinition() {
		return primary;
	}

	@Override
	public String getPrimaryPropositionId() {
		return primaryPropId;
	}

	@Override
	public List<PropositionDefinition> convert(
			ValueThresholdGroupEntity entity) {
		List<PropositionDefinition> result = new ArrayList<>();
		String propId = toPropositionId(entity);
		if (this.converterVisitor.addPropositionId(propId)) {
			CompoundLowLevelAbstractionDefinition wrapped = new CompoundLowLevelAbstractionDefinition(
					propId);
			wrapped.setDisplayName(entity.getDisplayName());
			wrapped.setDescription(entity.getDescription());

			if (entity.getThresholdsOperator().getName().equalsIgnoreCase("any")) {
				wrapped.setValueDefinitionMatchOperator(CompoundLowLevelAbstractionDefinition.ValueDefinitionMatchOperator.ANY);
			} else if (entity.getThresholdsOperator().getName()
					.equalsIgnoreCase("all")) {
				wrapped.setValueDefinitionMatchOperator(CompoundLowLevelAbstractionDefinition.ValueDefinitionMatchOperator.ALL);
			} else {
				throw new IllegalStateException("valueDefinitionMatchOperator"
						+ " can only be ANY or ALL");
			}

			wrapped.setGapFunction(new SimpleGapFunction(0, null));

			List<LowLevelAbstractionDefinition> intermediates = new ArrayList<>();
			for (ValueThresholdEntity v : entity.getValueThresholds()) {
				v.getAbstractedFrom().accept(this.converterVisitor);
				result.addAll(this.converterVisitor.getPropositionDefinitions());
				LowLevelAbstractionDefinition def =
						new LowLevelAbstractionDefinition(
						entity.getKey() + "_SUB" + v.getId());
				def.setConcatenable(false);
				def.addPrimitiveParameterId(this.converterVisitor.getPrimaryPropositionId());
				def.setMinimumNumberOfValues(1);
				def.setMaximumNumberOfValues(1);
				def.setAlgorithmId("stateDetector");
				def.setGapFunction(new SimpleGapFunction(Integer.valueOf(0), null));
				thresholdToValueDefinitions(entity, v, def);
				def.setSlidingWindowWidthMode(SlidingWindowWidthMode.DEFAULT);
				def.setGapFunction(new SimpleGapFunction(0, null));
				List<ExtendedPhenotype> extendedPhenotypes =
						v.getExtendedPhenotypes();
				if (extendedPhenotypes != null && !extendedPhenotypes.isEmpty()) {
					ContextDefinition cd = 
							extractContextDefinition(entity, 
							extendedPhenotypes, v);
					result.add(cd);
					def.setContextId(cd.getId());
				}
				def.setSourceId(sourceId(entity));
				intermediates.add(def);
			}
			result.addAll(intermediates);

			for (LowLevelAbstractionDefinition def : intermediates) {
				wrapped.addValueClassification(new ValueClassification(asValueString(entity),
						def.getId(), asValueString(entity)));
				wrapped.addValueClassification(new ValueClassification(asValueCompString(entity),
						def.getId(), asValueCompString(entity)));
			}

			wrapped.setSourceId(sourceId(entity));
			result.add(wrapped);
			this.primary = wrapped;
			this.primaryPropId = wrapped.getPropositionId();
		}

		return result;
	}
}
