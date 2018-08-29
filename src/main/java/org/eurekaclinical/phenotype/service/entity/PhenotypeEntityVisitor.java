/*
 * #%L
 * Eureka Common
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
package org.eurekaclinical.phenotype.service.entity;

//import org.eurekaclinical.phenotype.service.entity.CategoryEntity;
//import org.eurekaclinical.phenotype.service.entity.SequenceEntity;
//import org.eurekaclinical.phenotype.service.entity.SystemProposition;
//import org.eurekaclinical.phenotype.service.entity.ValueThresholdGroupEntity;

public interface PhenotypeEntityVisitor {
	
	public void visit(SystemProposition proposition);
	
	public void visit(CategoryEntity categorization);
	
	public void visit(SequenceEntity highLevelAbstraction);

	public void visit(ValueThresholdGroupEntity lowLevelAbstraction);

	public void visit(FrequencyEntity sliceAbstraction);

}
