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

import org.eurekaclinical.phenotype.client.comm.ThresholdsOperator;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;


//import org.eurekaclinical.phenotype.service.entity.CategoryEntity.CategoryType;

import javax.persistence.ManyToOne;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import org.eurekaclinical.phenotype.service.entity.CategoryEntity.CategoryType;

/**
 * Contains attributes which describe a Protempa low-level abstraction in the
 * context of the Eureka! UI.
 */
@Entity
@Table(name = "value_threshold_groups")
public class ValueThresholdGroupEntity extends PhenotypeEntity {

	/*
	 * The allowed values of the low-level abstraction
	 */
	@OneToMany(cascade = CascadeType.ALL)
	@JoinColumn(nullable = false, name = "valuethresholdsgroup_id")
	private List<ValueThresholdEntity> valueThresholds;
	
	@ManyToOne
	@JoinColumn(name = "thresholdsop_id", referencedColumnName = "id", nullable = false)
	private ThresholdsOperator thresholdsOperator;
	
	public ValueThresholdGroupEntity() {
		super(CategoryType.LOW_LEVEL_ABSTRACTION);
	}

	public List<ValueThresholdEntity> getValueThresholds() {
		return valueThresholds;
	}

	public void setValueThresholds(List<ValueThresholdEntity> valueThresholds) {
		this.valueThresholds = valueThresholds;
	}

	public ThresholdsOperator getThresholdsOperator() {
		return thresholdsOperator;
	}

	public void setThresholdsOperator(ThresholdsOperator thresholdsOp) {
		this.thresholdsOperator = thresholdsOp;
	}

	@Override
	public void accept(PhenotypeEntityVisitor visitor) {
		visitor.visit(this);
	}
	
	@Override
	public String toString() {
		return ReflectionToStringBuilder.reflectionToString(this);
	}

}
