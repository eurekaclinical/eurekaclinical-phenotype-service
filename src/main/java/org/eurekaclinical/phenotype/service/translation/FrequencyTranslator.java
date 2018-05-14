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

import javax.ws.rs.core.Response;


import com.google.inject.Inject;

import org.eurekaclinical.eureka.client.comm.Frequency;

import org.eurekaclinical.eureka.client.comm.exception.PhenotypeHandlingException;
import org.eurekaclinical.phenotype.service.dao.FrequencyTypeDao;
import org.eurekaclinical.phenotype.service.dao.TimeUnitDao;
import org.eurekaclinical.phenotype.service.dao.ValueComparatorDao;
import org.eurekaclinical.phenotype.service.entity.FrequencyEntity;
import org.eurekaclinical.phenotype.client.comm.TimeUnit;
import org.eurekaclinical.phenotype.service.finder.SystemPropositionFinder;
import org.eurekaclinical.phenotype.service.resource.SourceConfigResource;
import org.eurekaclinical.phenotype.service.dao.PhenotypeEntityDao;

public final class FrequencyTranslator implements
		PropositionTranslator<Frequency, FrequencyEntity> {

	private final TimeUnitDao timeUnitDao;
	private final ValueComparatorDao valueComparatorDao;
	private final FrequencyTypeDao freqTypeDao;
	private final TranslatorSupport translatorSupport;

	@Inject
	public FrequencyTranslator(PhenotypeEntityDao inPropositionDao,
			TimeUnitDao inTimeUnitDao, SystemPropositionFinder inFinder,
			ValueComparatorDao inValueComparatorDao,
			FrequencyTypeDao inFrequencyTypeDao,
			SourceConfigResource inSourceConfigResource) {
		this.timeUnitDao = inTimeUnitDao;
		this.translatorSupport = new TranslatorSupport(inPropositionDao,
				inFinder, inSourceConfigResource);
		this.valueComparatorDao = inValueComparatorDao;
		this.freqTypeDao = inFrequencyTypeDao;
	}

	@Override
	public FrequencyEntity translateFromPhenotype(Frequency phenotype)
			throws PhenotypeHandlingException {
		FrequencyEntity result =
				this.translatorSupport.getUserEntityInstance(phenotype,
				FrequencyEntity.class);

		result.setCount(phenotype.getAtLeast());
		result.setConsecutive(phenotype.getIsConsecutive());
		result.setExtendedProposition(PropositionTranslatorUtil
				.createOrUpdateExtendedProposition(
				result.getExtendedProposition(),
				phenotype.getPhenotype(), phenotype.getUserId(),
				timeUnitDao, translatorSupport,
				valueComparatorDao));
		result.setInSystem(false);
		result.setFrequencyType(
				freqTypeDao.retrieve(phenotype.getFrequencyType()));

		if (phenotype.getIsWithin()) {
			result.setWithinAtLeast(phenotype.getWithinAtLeast());
			Long withinAtLeastUnitsL = phenotype.getWithinAtLeastUnits();
			if (withinAtLeastUnitsL == null) {
				throw new PhenotypeHandlingException(
						Response.Status.PRECONDITION_FAILED,
						"Within at least units must be specified");
			}
			result.setWithinAtLeastUnits(
					this.timeUnitDao.retrieve(withinAtLeastUnitsL));
			result.setWithinAtMost(phenotype.getWithinAtMost());
			Long withinAtMostUnitsL = phenotype.getWithinAtMostUnits();
			if (withinAtMostUnitsL == null) {
				throw new PhenotypeHandlingException(
						Response.Status.PRECONDITION_FAILED,
						"Within at most units must be specified");
			}
			result.setWithinAtMostUnits(
					this.timeUnitDao.retrieve(withinAtMostUnitsL));
		} else {
			result.setWithinAtLeast(null);
			Long withinAtLeastUnitsL = phenotype.getWithinAtLeastUnits();
			TimeUnit withinAtLeastUnits;
			if (withinAtLeastUnitsL != null) {
				withinAtLeastUnits =
						this.timeUnitDao.retrieve(withinAtLeastUnitsL);
			} else {
				withinAtLeastUnits = this.timeUnitDao.getDefault();
			}
			result.setWithinAtLeastUnits(withinAtLeastUnits);
			result.setWithinAtMost(null);
			Long withinAtMostUnitsL = phenotype.getWithinAtMostUnits();
			TimeUnit withinAtMostUnits;
			if (withinAtMostUnitsL != null) {
				withinAtMostUnits =
						this.timeUnitDao.retrieve(withinAtMostUnitsL);
			} else {
				withinAtMostUnits = this.timeUnitDao.getDefault();

			}
			result.setWithinAtMostUnits(withinAtMostUnits);
		}

		return result;
	}

	@Override
	public Frequency translateFromProposition(FrequencyEntity entity) {
		Frequency result = new Frequency();

		PropositionTranslatorUtil.populateCommonPhenotypeFields(result,
				entity);
		result.setAtLeast(entity.getCount());
		result.setIsConsecutive(entity.isConsecutive());
		result.setPhenotype(PropositionTranslatorUtil
				.createPhenotypeField(entity.getExtendedProposition()));
		
		result.setWithinAtLeast(entity.getWithinAtLeast());
		result.setWithinAtLeastUnits(entity.getWithinAtLeastUnits().getId());
		result.setWithinAtMost(entity.getWithinAtMost());
		result.setWithinAtMostUnits(entity.getWithinAtMostUnits().getId());
		result.setFrequencyType(entity.getFrequencyType().getId());
		if (result.getWithinAtLeast() != null || result.getWithinAtMost() != null) {
			result.setIsWithin(true);
		}

		return result;
	}
}
