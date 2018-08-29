package org.eurekaclinical.phenotype.service.conversion;

/*
 * #%L
 * Eureka Services
 * %%
 * Copyright (C) 2012 - 2014 Emory University
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


import org.protempa.DefaultSourceId;
import org.protempa.SourceId;
import org.protempa.proposition.value.NominalValue;

import org.eurekaclinical.phenotype.service.entity.PhenotypeEntity;

/**
 *
 * @author Andrew Post
 */
public class AbstractConverter {
	private final PhenotypeConversionSupport conversionSupport;

	AbstractConverter() {
		this.conversionSupport = new PhenotypeConversionSupport();
	}
	
	protected String asValueString(PhenotypeEntity phenotype) {
		return this.conversionSupport.asValueString(phenotype);
	}
	
	protected String asValueCompString(PhenotypeEntity phenotype) {
		return this.conversionSupport.asValueCompString(phenotype);
	}
	
	protected NominalValue asValue(PhenotypeEntity phenotype) {
		return this.conversionSupport.asValue(phenotype);
	}
	
	protected String toPropositionId(PhenotypeEntity phenotype) {
		return this.conversionSupport.toPropositionId(phenotype);
	}
	
	protected String toPropositionIdWrapped(PhenotypeEntity phenotype) {
		return this.conversionSupport.toPropositionIdWrapped(phenotype);
	}
	
	protected SourceId sourceId(PhenotypeEntity phenotype) {
		return DefaultSourceId.getInstance("Eureka");
	}
}
