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
import org.eurekaclinical.phenotype.service.entity.ExtendedPhenotype;
import org.eurekaclinical.phenotype.service.entity.Relation;
import org.eurekaclinical.phenotype.service.entity.SequenceEntity;
import org.protempa.HighLevelAbstractionDefinition;
import org.protempa.PropositionDefinition;
import org.protempa.TemporalExtendedPropositionDefinition;

import java.util.ArrayList;
import java.util.List;

import static org.eurekaclinical.phenotype.service.conversion.ConversionUtil.unit;
import java.util.HashMap;
import java.util.Map;
import org.protempa.proposition.interval.Interval.Side;
import org.protempa.SimpleGapFunction;
import org.protempa.TemporalPatternOffset;

final class SequenceConverter extends AbstractConverter
		implements
		PropositionDefinitionConverter<SequenceEntity, HighLevelAbstractionDefinition> {

	private PropositionDefinitionConverterVisitor converterVisitor;
	private HighLevelAbstractionDefinition primary;
	private String primaryPropId;
	private final Map<Long, TemporalExtendedPropositionDefinition> extendedProps;

	public SequenceConverter() {
		this.extendedProps = new HashMap<>();
	}

	@Override
	public HighLevelAbstractionDefinition getPrimaryPropositionDefinition() {
		return primary;
	}
	
	@Override
	public String getPrimaryPropositionId() {
		return primaryPropId;
	}

	public void setConverterVisitor(PropositionDefinitionConverterVisitor inVisitor) {
		converterVisitor = inVisitor;
	}

	@Override
	public List<PropositionDefinition> convert(SequenceEntity sequenceEntity) {
		List<PropositionDefinition> result = new ArrayList<>();
		String propId = toPropositionId(sequenceEntity);
		this.primaryPropId = propId;
		if (this.converterVisitor.addPropositionId(propId)) {
			HighLevelAbstractionDefinition p = new HighLevelAbstractionDefinition(
					propId);
			TemporalExtendedPropositionDefinition primaryEP = 
					buildExtendedProposition(
					sequenceEntity.getPrimaryExtendedPhenotype());
			if (sequenceEntity.getRelations() != null) {
				for (Relation rel : sequenceEntity.getRelations()) {
					PhenotypeEntity lhs =
							rel.getLhsExtendedPhenotype().getPhenotypeEntity();
					lhs.accept(converterVisitor);
					result.addAll(
							converterVisitor.getPropositionDefinitions());
					TemporalExtendedPropositionDefinition tepdLhs = buildExtendedProposition(rel
							.getLhsExtendedPhenotype());

					PhenotypeEntity rhs =
							rel.getRhsExtendedPhenotype().getPhenotypeEntity();
					rhs.accept(converterVisitor);
					result.addAll(converterVisitor.getPropositionDefinitions());
					TemporalExtendedPropositionDefinition tepdRhs = buildExtendedProposition(rel
							.getRhsExtendedPhenotype());

					p.add(tepdLhs);
					p.add(tepdRhs);
					p.setRelation(tepdLhs, tepdRhs, buildRelation(rel));
				}
			}
			
			p.setConcatenable(false);
			p.setSolid(false);
			p.setGapFunction(new SimpleGapFunction(0, null));
			
			TemporalPatternOffset temporalOffsets = new TemporalPatternOffset();
			temporalOffsets.setStartTemporalExtendedPropositionDefinition(primaryEP);
			temporalOffsets.setStartIntervalSide(Side.START);
			temporalOffsets.setStartOffset(0);
			temporalOffsets.setStartOffsetUnits(null);
			temporalOffsets.setFinishTemporalExtendedPropositionDefinition(primaryEP);
			temporalOffsets.setFinishIntervalSide(Side.FINISH);
			temporalOffsets.setFinishOffset(0);
			temporalOffsets.setFinishOffsetUnits(null);
			p.setTemporalOffset(temporalOffsets);
			p.setDisplayName(sequenceEntity.getDisplayName());
			p.setDescription(sequenceEntity.getDescription());
			p.setSourceId(sourceId(sequenceEntity));
			this.primary = p;
			result.add(p);
		}
		
		
		return result;
	}

	private TemporalExtendedPropositionDefinition buildExtendedProposition(
			ExtendedPhenotype ep) {
		TemporalExtendedPropositionDefinition tepd =
				this.extendedProps.get(ep.getId());
		if (tepd == null) {
			tepd = 
					ConversionUtil.buildExtendedPropositionDefinition(
					ep);

			this.extendedProps.put(ep.getId(), tepd);
		}

		return tepd;
	}

	private org.protempa.proposition.interval.Relation buildRelation(
			Relation rel) {
		return new org.protempa.proposition.interval.Relation(
				null, null, null, null, 
				rel.getMins1f2(), 
				unit(rel.getMins1f2TimeUnit()),
				rel.getMaxs1f2(), 
				unit(rel.getMaxs1f2TimeUnit()),
				rel.getMinf1s2(), 
				unit(rel.getMinf1s2TimeUnit()),
				rel.getMaxf1s2(), 
				unit(rel.getMaxf1s2TimeUnit()), 
				null, null, null, null);
	}
}
