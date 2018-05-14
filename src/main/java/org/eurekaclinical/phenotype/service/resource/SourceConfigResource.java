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

import java.util.ArrayList;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import org.arp.javautil.string.StringUtil;

import com.google.inject.Inject;
import com.sun.jersey.api.client.ClientResponse;

import org.eurekaclinical.eureka.client.comm.FileSourceConfigOption;
import org.eurekaclinical.eureka.client.comm.SourceConfig;
import org.eurekaclinical.eureka.client.comm.SourceConfigOption;
import org.eurekaclinical.eureka.client.comm.SourceConfig.Section;
import org.eurekaclinical.eureka.client.comm.SourceConfigParams;
import org.eurekaclinical.eureka.client.comm.SourceConfigParams.Upload;
import org.eurekaclinical.protempa.client.EurekaClinicalProtempaClient;
import org.eurekaclinical.eureka.client.comm.SystemPhenotype;
import org.eurekaclinical.common.comm.clients.ClientException;
import org.eurekaclinical.standardapis.exception.HttpStatusException;

/**
 * @author Andrew Post
 */
@Path("/protected/sourceconfig")
@RolesAllowed({"researcher"})
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SourceConfigResource {

	private final EurekaClinicalProtempaClient etlClient;

	@Inject
	public SourceConfigResource(EurekaClinicalProtempaClient inEtlClient) {
		this.etlClient = inEtlClient;
	}

	/**
	 * Gets all of the system elements for a user
	 *
	 * @return a {@link List} of {@link SystemPhenotype}s
	 */
	@GET
	public List<SourceConfig> getAll() {
		List<SourceConfig> sources;
		try {
			sources = this.etlClient.getSourceConfigs();
		} catch (ClientException ex) {
			throw new HttpStatusException(Status.INTERNAL_SERVER_ERROR, ex);
		}
		return sources;
	}

	@GET
	@Path("/{id}")
	public SourceConfig get(@PathParam("id") String inId) {
		try {
			return this.etlClient.getSourceConfig(inId);
		} catch (ClientException ex) {
			if (ex.getResponseStatus() == ClientResponse.Status.NOT_FOUND) {
				throw new HttpStatusException(Status.NOT_FOUND);
			} else {
				throw new HttpStatusException(Status.INTERNAL_SERVER_ERROR, ex);
			}
		}
	}

	@GET
	@Path("/parameters/list")
	public List<SourceConfigParams> getParamsList() {
		List<SourceConfigParams> result = new ArrayList<>();
		try {
			for (SourceConfig config : this.etlClient.getSourceConfigs()) {
				result.add(toParams(config));
			}
		} catch (ClientException ex) {
			throw new HttpStatusException(Status.INTERNAL_SERVER_ERROR, ex);
		}
		return result;
	}

	@GET
	@Path("/parameters/{id}")
	public SourceConfigParams getParams(@PathParam("id") String inId) {
		try {
			return toParams(this.etlClient.getSourceConfig(inId));
		} catch (ClientException ex) {
			if (ex.getResponseStatus() == ClientResponse.Status.NOT_FOUND) {
				throw new HttpStatusException(Status.NOT_FOUND);
			} else {
				throw new HttpStatusException(Status.INTERNAL_SERVER_ERROR, ex);
			}
		}
	}

	private static SourceConfigParams toParams(SourceConfig config) {
		SourceConfigParams params = new SourceConfigParams();
		params.setId(config.getId());
		String displayName = config.getDisplayName();
		if (StringUtil.getEmptyOrNull(displayName)) {
			displayName = config.getId();
		}
		params.setName(displayName);
		List<Upload> uploads = new ArrayList<>();
		for (Section section : config.getDataSourceBackends()) {
			Upload upload = null;
			String sourceId = null;
			String sampleUrl = null;
			for (SourceConfigOption option : section.getOptions()) {
				if (option instanceof FileSourceConfigOption) {
					upload = new Upload();
					upload.setName(section.getDisplayName());
					upload.setAcceptedMimetypes(((FileSourceConfigOption) option).getAcceptedMimetypes());
					if (sourceId != null) {
						upload.setSourceId(sourceId);
					}
					if (sampleUrl != null) {
						upload.setSampleUrl(sampleUrl);
					}
					upload.setRequired(option.isRequired());
				} else if (option.getName().equals("dataFileDirectoryName")) {
					Object val = option.getValue();
					if (val != null) {
						sourceId = val.toString();
						if (upload != null) {
							upload.setSourceId(sourceId);
						}
					}
				} else if (option.getName().equals("sampleUrl")) {
					Object val = option.getValue();
					if (val != null) {
						sampleUrl = val.toString();
						if (upload != null) {
							upload.setSampleUrl(sampleUrl);
						}
					}
				}
			}
			if (upload != null) {
				uploads.add(upload);
			}
		}
		params.setUploads(uploads.toArray(new Upload[uploads.size()]));
		return params;
	}
}
