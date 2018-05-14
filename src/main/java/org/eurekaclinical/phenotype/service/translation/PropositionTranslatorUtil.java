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

import org.eurekaclinical.eureka.client.comm.Phenotype;
import org.eurekaclinical.eureka.client.comm.PhenotypeField;
//import edu.emory.cci.aiw.cvrg.eureka.common.entity.PhenotypeEntity;
//import edu.emory.cci.aiw.cvrg.eureka.common.entity.ExtendedPhenotype;
//import edu.emory.cci.aiw.cvrg.eureka.common.entity.PropertyConstraint;
//import edu.emory.cci.aiw.cvrg.eureka.common.entity.PropositionTypeVisitor;
//import edu.emory.cci.aiw.cvrg.eureka.common.entity.TimeUnit;
//import edu.emory.cci.aiw.cvrg.eureka.common.entity.ValueComparator;
import org.eurekaclinical.eureka.client.comm.exception.PhenotypeHandlingException;

//import edu.emory.cci.aiw.cvrg.eureka.common.entity.ExtendedPhenotype;
import org.eurekaclinical.phenotype.service.dao.TimeUnitDao;
import org.eurekaclinical.phenotype.service.dao.ValueComparatorDao;
import org.eurekaclinical.phenotype.service.entity.ExtendedPhenotype;
import org.eurekaclinical.phenotype.service.entity.PhenotypeEntity;
import org.eurekaclinical.phenotype.service.entity.PropertyConstraint;
import org.eurekaclinical.phenotype.service.entity.PropositionTypeVisitor;
import org.eurekaclinical.phenotype.client.comm.TimeUnit;
import org.eurekaclinical.phenotype.client.comm.ValueComparator;

/**
 * Contains common utility functions for all implementations of
 * {@link PropositionTranslator}.
 */
class PropositionTranslatorUtil {

	static void populateExtendedProposition(ExtendedPhenotype ep, 
			PhenotypeEntity proposition, PhenotypeField phenotype, 
			TimeUnitDao timeUnitDao, ValueComparatorDao valCompDao) 
			throws PhenotypeHandlingException {
		ep.setPhenotypeEntity(proposition);
		if (phenotype.getHasDuration() == Boolean.TRUE) {
			ep.setMinDuration(phenotype.getMinDuration());
			Long minDurationUnits = phenotype.getMinDurationUnits();
			if (minDurationUnits == null) {
				throw new PhenotypeHandlingException(
						Response.Status.BAD_REQUEST, 
						"Min duration units must be specified");
			}
			ep.setMinDurationTimeUnit(timeUnitDao.retrieve(minDurationUnits));
			ep.setMaxDuration(phenotype.getMaxDuration());
			Long maxDurationUnits = phenotype.getMaxDurationUnits();
			if (maxDurationUnits == null) {
				throw new PhenotypeHandlingException(
						Response.Status.BAD_REQUEST, 
						"Max duration units must be specified");
			}
			ep.setMaxDurationTimeUnit(timeUnitDao.retrieve(maxDurationUnits));
		} else {
			ep.setMinDuration(null);
			Long minDurationUnitsL = phenotype.getMinDurationUnits();
			TimeUnit minDurationUnits;
			if (minDurationUnitsL != null) {
				minDurationUnits = timeUnitDao.retrieve(minDurationUnitsL);
			} else {
				minDurationUnits = timeUnitDao.getDefault();
			}
			ep.setMinDurationTimeUnit(minDurationUnits);
			ep.setMaxDuration(null);
			Long maxDurationUnitsL = phenotype.getMaxDurationUnits();
			TimeUnit maxDurationUnits;
			if (maxDurationUnitsL != null) {
				maxDurationUnits = timeUnitDao.retrieve(maxDurationUnitsL);
			} else {
				maxDurationUnits = timeUnitDao.getDefault();
			}
			ep.setMaxDurationTimeUnit(maxDurationUnits);
		}
		if (phenotype.getHasPropertyConstraint() == Boolean.TRUE) {
			String propertyName = phenotype.getProperty();
			if (propertyName == null) {
				throw new PhenotypeHandlingException(
						Response.Status.BAD_REQUEST, 
						"A property name must be specified");
			}
			PropertyConstraint pc = ep.getPropertyConstraint();
			if (pc == null) {
				pc = new PropertyConstraint();
			}
			pc.setPropertyName(propertyName);
			pc.setValue(phenotype.getPropertyValue());
			ValueComparator valComp = valCompDao.getByName("=");
			if (valComp == null) {
				throw new AssertionError("Invalid value comparator: =");
			}
			pc.setValueComparator(valComp);
			ep.setPropertyConstraint(pc);
		} else {
			ep.setPropertyConstraint(null);
		}
	}

	private PropositionTranslatorUtil() {
		// prevents instantiation
	}

	/**
	 * Populates the fields common to all phenotypes based on the given
	 * proposition.
	 * 
	 * @param phenotype
	 *            the {@link Phenotype} to populate. Modified as a result of
	 *            calling this method.
	 * @param proposition
	 *            the {@link PhenotypeEntity} to get the data from
	 */
	static void populateCommonPhenotypeFields(Phenotype phenotype,
	        PhenotypeEntity proposition) {
		phenotype.setId(proposition.getId());
		phenotype.setKey(proposition.getKey());
		phenotype.setDisplayName(proposition.getDisplayName());
		phenotype.setDescription(proposition.getDescription());
		phenotype.setCreated(proposition.getCreated());
		phenotype.setLastModified(proposition.getLastModified());
		phenotype.setUserId(proposition.getUserId());
		phenotype.setInSystem(proposition.isInSystem());
	}

	static ExtendedPhenotype createOrUpdateExtendedProposition(
			ExtendedPhenotype origEP,
	        PhenotypeField phenotype, Long userId, 
			TimeUnitDao timeUnitDao, 
			TranslatorSupport translatorSupport,
			ValueComparatorDao valCompDao) throws PhenotypeHandlingException {

		ExtendedPhenotype ep = origEP;
		if (ep == null) {
			ep = new ExtendedPhenotype();
		}
		PhenotypeEntity proposition = 
				translatorSupport.getUserOrSystemEntityInstance(userId, 
				phenotype.getPhenotypeKey());
		populateExtendedProposition(ep, proposition, phenotype, timeUnitDao, valCompDao);

		return ep;
	}
	
	static PhenotypeField createPhenotypeField(ExtendedPhenotype ep) {
		PhenotypeField phenotype = new PhenotypeField();
		phenotype.setPhenotypeKey(ep.getPhenotypeEntity().getKey());
		phenotype.setPhenotypeDescription(ep.getPhenotypeEntity()
				.getDescription());
		phenotype.setPhenotypeDisplayName(ep.getPhenotypeEntity()
				.getDisplayName());
		PropositionTypeVisitor v = new PropositionTypeVisitor();
		ep.getPhenotypeEntity().accept(v);
		phenotype.setType(v.getType());
		if (ep.getMinDuration() != null) {
			phenotype.setHasDuration(true);
			phenotype.setMinDuration(ep.getMinDuration());
			phenotype.setMinDurationUnits(ep.getMinDurationTimeUnit()
			        .getId());
		}
		if (ep.getMaxDuration() != null) {
			phenotype.setHasDuration(true);
			phenotype.setMaxDuration(ep.getMaxDuration());
			phenotype.setMaxDurationUnits(ep.getMaxDurationTimeUnit()
			        .getId());
		}
		if (ep.getPropertyConstraint() != null) {
			phenotype.setHasPropertyConstraint(true);
			phenotype.setProperty(ep.getPropertyConstraint()
			        .getPropertyName());
			phenotype.setPropertyValue(ep.getPropertyConstraint().getValue());
		}
		return phenotype;
	}
}
