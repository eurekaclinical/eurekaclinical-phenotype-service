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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import org.eurekaclinical.eureka.client.comm.PhenotypeField;
import org.eurekaclinical.eureka.client.comm.ValueThreshold;
import org.eurekaclinical.eureka.client.comm.ValueThresholds;
//import edu.emory.cci.aiw.cvrg.eureka.common.entity.*;
import org.eurekaclinical.eureka.client.comm.exception.PhenotypeHandlingException;
import org.eurekaclinical.phenotype.service.dao.RelationOperatorDao;
import org.eurekaclinical.phenotype.service.dao.ThresholdsOperatorDao;
import org.eurekaclinical.phenotype.service.dao.TimeUnitDao;
import org.eurekaclinical.phenotype.service.dao.ValueComparatorDao;
import org.eurekaclinical.phenotype.service.entity.ExtendedPhenotype;
import org.eurekaclinical.phenotype.service.entity.PhenotypeEntity;
import org.eurekaclinical.phenotype.service.entity.PropositionTypeVisitor;
import org.eurekaclinical.phenotype.service.entity.ValueThresholdEntity;
import org.eurekaclinical.phenotype.service.entity.ValueThresholdGroupEntity;

public final class ValueThresholdsTranslator implements
		PropositionTranslator<ValueThresholds, ValueThresholdGroupEntity> {

	private static final Logger LOGGER = LoggerFactory.getLogger(
			ValueThresholdsTranslator.class);
	private final TranslatorSupport translatorSupport;
	private final ValueComparatorDao valueCompDao;
	private final ThresholdsOperatorDao thresholdsOperatorDao;
	private final TimeUnitDao timeUnitDao;
	private final RelationOperatorDao relationOpDao;

	@Inject
	public ValueThresholdsTranslator(ValueComparatorDao inValueComparatorDao,
			ThresholdsOperatorDao inThresholdsOperatorDao,
			TranslatorSupport inSupport,
			TimeUnitDao inTimeUnitDao,
			RelationOperatorDao inRelationOpDao) {
		valueCompDao = inValueComparatorDao;
		thresholdsOperatorDao = inThresholdsOperatorDao;
		translatorSupport = inSupport;
		timeUnitDao = inTimeUnitDao;
		relationOpDao = inRelationOpDao;
	}

	@Override
	public ValueThresholdGroupEntity translateFromPhenotype(
			ValueThresholds phenotype) throws PhenotypeHandlingException {
		if (phenotype == null) {
			throw new IllegalArgumentException("element cannot be null");
		}

		ValueThresholdGroupEntity result
				= this.translatorSupport.getUserEntityInstance(phenotype,
						ValueThresholdGroupEntity.class);

		result.setThresholdsOperator(thresholdsOperatorDao.retrieve(phenotype
				.getThresholdsOperator()));

		List<ValueThresholdEntity> thresholds = result.getValueThresholds();
		if (thresholds == null) {
			thresholds = new ArrayList<>();
			result.setValueThresholds(thresholds);
		}

		int i = 0;
		for (ValueThreshold vt : phenotype.getValueThresholds()) {
			ValueThresholdEntity vte;
			if (thresholds.size() > i) {
				vte = thresholds.get(i);
			} else {
				vte = new ValueThresholdEntity();
				thresholds.add(vte);
			}
			vte.setAbstractedFrom(translatorSupport.getUserOrSystemEntityInstance(
					phenotype.getUserId(), vt.getPhenotype()
					.getPhenotypeKey()));

			String lowerValue = vt.getLowerValue();
			vte.setMinTValueThreshold(lowerValue);
			if (lowerValue != null && !lowerValue.isEmpty()) {
				try {
					vte.setMinValueThreshold(new BigDecimal(lowerValue));
				} catch (NumberFormatException ex) {
					LOGGER.debug("Could not parse " + lowerValue
							+ " as a BigDecimal", ex);
				}
			}
			vte.setMinValueComp(valueCompDao.retrieve(vt.getLowerComp()));
			// vte.setMinUnits(vt.getLowerUnits());

			String upperValue = vt.getUpperValue();
			vte.setMaxTValueThreshold(upperValue);
			if (upperValue != null && !upperValue.isEmpty()) {
				try {
					vte.setMaxValueThreshold(new BigDecimal(upperValue));
				} catch (NumberFormatException ex) {
					LOGGER.debug("Could not parse " + upperValue
							+ " as a BigDecimal", ex);
				}
			}
			vte.setMaxValueComp(valueCompDao.retrieve(vt.getUpperComp()));
			// vte.setMaxUnits(vt.getUpperUnits());
			List<ExtendedPhenotype> extendedPhenotypes
					= vte.getExtendedPhenotypes();

			vte.setRelationOperator(
					relationOpDao.retrieve(vt.getRelationOperator()));
			vte.setWithinAtLeast(vt.getWithinAtLeast());
			vte.setWithinAtLeastUnits(
					timeUnitDao.retrieve(vt.getWithinAtLeastUnit()));
			vte.setWithinAtMost(vt.getWithinAtMost());
			vte.setWithinAtMostUnits(
					timeUnitDao.retrieve(vt.getWithinAtMostUnit()));

			if (extendedPhenotypes == null) {
				extendedPhenotypes = new ArrayList<>();
				vte.setExtendedPhenotypes(extendedPhenotypes);
			}
			int j = 0;
			List<PhenotypeField> relatedPhenotypes = vt.getRelatedPhenotypes();
			if (relatedPhenotypes != null) {
				for (PhenotypeField de : relatedPhenotypes) {
					ExtendedPhenotype ede = null;
					if (extendedPhenotypes.size() > j) {
						ede = extendedPhenotypes.get(j);
						PropositionTranslatorUtil
								.createOrUpdateExtendedProposition(
										ede,
										de, phenotype.getUserId(),
										timeUnitDao, translatorSupport,
										valueCompDao);
					} else {
						extendedPhenotypes.add(PropositionTranslatorUtil
								.createOrUpdateExtendedProposition(
										ede,
										de, phenotype.getUserId(),
										timeUnitDao, translatorSupport,
										valueCompDao));
					}
					j++;
				}
			}
			for (int k = extendedPhenotypes.size() - 1; k >= j; k--) {
				extendedPhenotypes.remove(k);
			}

			i++;
		}
		result.setValueThresholds(thresholds);

		return result;
	}

	@Override
	public ValueThresholds translateFromProposition(
			ValueThresholdGroupEntity entity) {
		ValueThresholds result = new ValueThresholds();

		PropositionTranslatorUtil.populateCommonPhenotypeFields(result,
				entity);

		result.setThresholdsOperator(entity.getThresholdsOperator().getId());

		List<ValueThreshold> thresholds = new ArrayList<>();
		for (ValueThresholdEntity vte : entity.getValueThresholds()) {
			ValueThreshold threshold = new ValueThreshold();
			threshold.setLowerValue(vte.getMinTValueThreshold());
			threshold.setLowerComp(vte.getMinValueComp().getId());
			// threshold.setLowerUnits(vte.getMinUnits());

			threshold.setUpperValue(vte.getMaxTValueThreshold());
			threshold.setUpperComp(vte.getMaxValueComp().getId());
			// threshold.setUpperUnits(vte.getMaxUnits());

			PhenotypeEntity phenotypeEntity = vte.getAbstractedFrom();
			PhenotypeField elementField = new PhenotypeField();
			PropositionTypeVisitor visitor = new PropositionTypeVisitor();
			phenotypeEntity.accept(visitor);
			elementField.setType(visitor.getType());
			elementField.setPhenotypeDescription(phenotypeEntity
					.getDescription());
			elementField.setPhenotypeDisplayName(phenotypeEntity
					.getDisplayName());
			elementField.setPhenotypeKey(phenotypeEntity.getKey());
			threshold.setPhenotype(elementField);

			List<PhenotypeField> relatedPhenotypes
					= new ArrayList<>();
			for (ExtendedPhenotype elt : vte.getExtendedPhenotypes()) {
				PhenotypeField phenotypeField
						= PropositionTranslatorUtil.createPhenotypeField(elt);
				relatedPhenotypes.add(phenotypeField);
			}
			threshold.setRelatedPhenotypes(relatedPhenotypes);

			threshold.setWithinAtLeast(vte.getWithinAtLeast());
			threshold.setWithinAtLeastUnit(vte.getWithinAtLeastUnits().getId());
			threshold.setWithinAtMost(vte.getWithinAtMost());
			threshold.setWithinAtMostUnit(vte.getWithinAtMostUnits().getId());
			threshold.setRelationOperator(vte.getRelationOperator().getId());
			thresholds.add(threshold);
		}
		result.setValueThresholds(thresholds);

		return result;
	}
}
