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
package org.eurekaclinical.phenotype.service.resource;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.commons.lang3.StringUtils;

import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import com.sun.javafx.scene.control.skin.VirtualFlow;

import org.eurekaclinical.eureka.client.comm.Phenotype;
import org.eurekaclinical.phenotype.service.entity.PhenotypeEntity;
import org.eurekaclinical.phenotype.service.entity.PropositionChildrenVisitor;
import org.eurekaclinical.eureka.client.comm.exception.PhenotypeHandlingException;
import org.eurekaclinical.phenotype.service.dao.AuthorizedUserDao;
import org.eurekaclinical.phenotype.service.translation.PhenotypeEntityTranslatorVisitor;
import org.eurekaclinical.phenotype.service.translation.PhenotypeTranslatorVisitor;
import org.eurekaclinical.phenotype.service.translation.SummarizingPhenotypeEntityTranslatorVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.eurekaclinical.phenotype.service.dao.PhenotypeEntityDao;
import java.net.URI;
import org.eurekaclinical.phenotype.service.conversion.ConversionSupport;
import org.eurekaclinical.phenotype.service.conversion.PropositionDefinitionCollector;
import org.eurekaclinical.phenotype.service.conversion.PropositionDefinitionConverterVisitor;
import org.eurekaclinical.phenotype.service.entity.AuthorizedUserEntity;
import org.eurekaclinical.standardapis.exception.HttpStatusException;
import org.protempa.PropositionDefinition;

/**
 * PropositionCh
 *
 * @author hrathod
 */
@Transactional
@Path("/protected/phenotypes2prop")
@RolesAllowed({"admin"})
@Consumes(MediaType.APPLICATION_JSON)
public class phenotypes2propResource {
    
	private static final Logger LOGGER
			= LoggerFactory.getLogger(phenotypes2propResource.class);
        
	private static final ResourceBundle messages
			= ResourceBundle.getBundle("Messages");
	private final PhenotypeEntityDao phenotypeEntityDao;
	private final AuthorizedUserDao userDao;
	private final PhenotypeEntityTranslatorVisitor pETranslatorVisitor;
	private final PhenotypeTranslatorVisitor phenotypeTranslatorVisitor;
	private final SummarizingPhenotypeEntityTranslatorVisitor summpETranslatorVisitor;
        private final PropositionDefinitionConverterVisitor converterVisitor;
        private final ConversionSupport conversionSupport;
        
	@Inject
	public phenotypes2propResource(PhenotypeEntityDao inDao, AuthorizedUserDao inUserDao,
			PhenotypeEntityTranslatorVisitor inPETranslatorVisitor,
			SummarizingPhenotypeEntityTranslatorVisitor inSummpETranslatorVisitor,
			PhenotypeTranslatorVisitor inPhenotypeTranslatorVisitor,
                        PropositionDefinitionConverterVisitor inVisitor) {
		this.phenotypeEntityDao = inDao;
		this.pETranslatorVisitor = inPETranslatorVisitor;
		this.summpETranslatorVisitor = inSummpETranslatorVisitor;
		this.phenotypeTranslatorVisitor = inPhenotypeTranslatorVisitor;
		this.userDao = inUserDao;
                this.converterVisitor = inVisitor;
                this.conversionSupport = new ConversionSupport();
	}
        
        @GET
	@Produces(MediaType.APPLICATION_JSON)
	public List<PropositionDefinition> getAllToProp(@Context HttpServletRequest inRequest) {
		AuthorizedUserEntity user = this.userDao.getByHttpServletRequest(inRequest);
		List<PropositionDefinition> result;
		List<PhenotypeEntity> phenotypeEntities
				= this.phenotypeEntityDao.getByUserId(user.getId());
                
                PropositionDefinitionCollector collector
				= PropositionDefinitionCollector.getInstance(
						this.converterVisitor, phenotypeEntities);
		result = collector.getUserPropDefs();
		return result;
	}
}
