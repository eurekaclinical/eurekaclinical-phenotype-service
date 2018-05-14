package org.eurekaclinical.phenotype.service.config;

/*-
 * #%L
 * eurekaclinical-phenotype-service
 * %%
 * Copyright (C) 2018 Emory University
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

import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.persist.jpa.JpaPersistModule;
import com.google.inject.servlet.GuiceServletContextListener;
import org.eurekaclinical.common.config.InjectorSupport;
import org.eurekaclinical.common.config.ServiceServletModule;
import org.eurekaclinical.phenotype.service.props.ServiceProperties;

/**
 * Loaded on application startup, this class creates the Guice injector. If
 * this service accesses any 
 * {@link org.eurekaclinical.common.comm.clients.EurekaClinicalClient} REST API 
 * clients, override {@link #contextInitialized}, and in addition to calling
 * the superclass method, create a 
 * {@link org.eurekaclinical.common.config.ClientSessionListener} for each
 * client and add it to the servlet context.
 */
public class ContextListener extends GuiceServletContextListener {
    private static final String PACKAGE_NAMES = "org.eurekaclinical.service.resource";
    private static final String JPA_UNIT = "service-jpa-unit";
    
    private final ServiceProperties properties;
    
    /**
     * Constructs an instance of this class.
     */
    public ContextListener() {
        this.properties = new ServiceProperties();
    }

    /**
     * Binds any classes, sets up JPA persistence, and creates and returns the
     * Guice injector.
     * 
     * @return the Guice injector
     */
    @Override
    protected Injector getInjector() {
        return new InjectorSupport(
            new Module[]{
                new AppModule(),
                new ServiceServletModule(this.properties, PACKAGE_NAMES),
                new JpaPersistModule(JPA_UNIT)
            },
            this.properties).getInjector();
    }
}
