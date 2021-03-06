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
package org.eurekaclinical.phenotype.service.finder;

/**
 * Represents errors in retrieving proposition definitions.
 * 
 * @author Andrew Post
 */
public class PropositionFindException extends Exception {

	public PropositionFindException() {
	}

	public PropositionFindException(String string) {
		super(string);
	}

	public PropositionFindException(String string, Throwable thrwbl) {
		super(string, thrwbl);
	}

	public PropositionFindException(Throwable thrwbl) {
		super(thrwbl);
	}
	
}
