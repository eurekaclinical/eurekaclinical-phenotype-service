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
import org.eurekaclinical.eureka.client.comm.Category;
import org.eurekaclinical.eureka.client.comm.Category.CategoricalType;
import org.eurekaclinical.eureka.client.comm.PhenotypeField;
//import edu.emory.cci.aiw.cvrg.eureka.common.entity.CategoryEntity;
//import edu.emory.cci.aiw.cvrg.eureka.common.entity.CategoryEntity.CategoryType;
//import edu.emory.cci.aiw.cvrg.eureka.common.entity.PhenotypeEntity;
//import edu.emory.cci.aiw.cvrg.eureka.common.entity.PropositionTypeVisitor;
import org.eurekaclinical.eureka.client.comm.exception.PhenotypeHandlingException;
import org.eurekaclinical.phenotype.service.finder.SystemPropositionFinder;
import org.eurekaclinical.phenotype.service.resource.SourceConfigResource;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import javax.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;
import org.eurekaclinical.phenotype.service.dao.PhenotypeEntityDao;
import org.eurekaclinical.phenotype.service.entity.CategoryEntity;
import org.eurekaclinical.phenotype.service.entity.CategoryEntity.CategoryType;
import org.eurekaclinical.phenotype.service.entity.PhenotypeEntity;
import org.eurekaclinical.phenotype.service.entity.PropositionTypeVisitor;

/**
 * Translates categorical phenotypes (UI phenotype) into categorization
 * propositions.
 */
public final class CategorizationTranslator implements
		PropositionTranslator<Category, CategoryEntity> {

	private final TranslatorSupport translatorSupport;

	@Inject
	public CategorizationTranslator(
			PhenotypeEntityDao inPropositionDao,
			SystemPropositionFinder inFinder,
			SourceConfigResource inSourceConfigResource) {
		this.translatorSupport =
				new TranslatorSupport(inPropositionDao, inFinder, inSourceConfigResource);
	}

	@Override
	public CategoryEntity translateFromPhenotype(Category phenotype)
			throws PhenotypeHandlingException {
		CategoryEntity result =
				this.translatorSupport.getUserEntityInstance(phenotype,
				CategoryEntity.class);

		List<PhenotypeEntity> inverseIsA = new ArrayList<>();
		if (phenotype.getChildren() != null) {
			for (PhenotypeField de : phenotype.getChildren()) {
				PhenotypeEntity proposition =
						this.translatorSupport.getUserOrSystemEntityInstance(
						phenotype.getUserId(), de.getPhenotypeKey());
				inverseIsA.add(proposition);
			}
		}
		result.setMembers(inverseIsA);
		result.setCategoryType(checkPropositionType(phenotype, inverseIsA));

		return result;
	}

	private CategoryType checkPropositionType(Category phenotype,
			List<PhenotypeEntity> inverseIsA)
			throws PhenotypeHandlingException {
		if (inverseIsA.isEmpty()) {
			throw new PhenotypeHandlingException(
					Response.Status.PRECONDITION_FAILED, "Category "
					+ phenotype.getKey()
					+ " is invalid because it has no children");
		}
		Set<CategoryType> categorizationTypes =
				EnumSet.noneOf(CategoryType.class);
		for (PhenotypeEntity phenotypeEntity : inverseIsA) {
			categorizationTypes.add(phenotypeEntity.getCategoryType());
		}
		if (categorizationTypes.size() > 1) {
			throw new PhenotypeHandlingException(
					Response.Status.PRECONDITION_FAILED, "Category "
					+ phenotype.getKey()
					+ " has children with inconsistent types: "
					+ StringUtils.join(categorizationTypes, ", "));
		}
		return categorizationTypes.iterator().next();
	}

	@Override
	public Category translateFromProposition(
			CategoryEntity proposition) {
		Category result = new Category();

		PropositionTranslatorUtil.populateCommonPhenotypeFields(result,
				proposition);
		List<PhenotypeField> children = new ArrayList<>();
		for (PhenotypeEntity p : proposition.getMembers()) {
			PhenotypeField def = new PhenotypeField();
			def.setPhenotypeKey(p.getKey());
			def.setPhenotypeDisplayName(p.getDisplayName());
			def.setPhenotypeDescription(p.getDescription());
			PropositionTypeVisitor visitor = new PropositionTypeVisitor();
			p.accept(visitor);
			def.setType(visitor.getType());
			if (p instanceof CategoryEntity) {
				def.setCategoricalType(checkElementType((CategoryEntity) p));
			}
			children.add(def);
		}
		
		result.setChildren(children);
		result.setCategoricalType(checkElementType(proposition));

		return result;
	}

	private CategoricalType checkElementType(CategoryEntity proposition) {
		switch (proposition.getCategoryType()) {
			case LOW_LEVEL_ABSTRACTION:
				return CategoricalType.LOW_LEVEL_ABSTRACTION;
			case HIGH_LEVEL_ABSTRACTION:
				return CategoricalType.HIGH_LEVEL_ABSTRACTION;
			case SLICE_ABSTRACTION:
				return CategoricalType.SLICE_ABSTRACTION;
			case CONSTANT:
				return CategoricalType.CONSTANT;
			case EVENT:
				return CategoricalType.EVENT;
			case PRIMITIVE_PARAMETER:
				return CategoricalType.PRIMITIVE_PARAMETER;
			case SEQUENTIAL_TEMPORAL_PATTERN_ABSTRACTION:
				return CategoricalType.SEQUENTIAL_TEMPORAL_PATTERN_ABSTRACTION;
			default:
				throw new AssertionError("Invalid category type: "
						+ proposition.getCategoryType());
		}
	}
}
