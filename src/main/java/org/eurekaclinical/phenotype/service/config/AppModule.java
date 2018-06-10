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

import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import javax.naming.Context;
import javax.naming.InitialContext;
import org.eurekaclinical.phenotype.service.dao.AuthorizedUserDao;
import org.eurekaclinical.phenotype.service.dao.FrequencyTypeDao;
import org.eurekaclinical.phenotype.service.dao.JpaFrequencyTypeDao;
import org.eurekaclinical.phenotype.service.dao.JpaPhenotypeEntityDao;
import org.eurekaclinical.phenotype.service.dao.JpaRelationOperatorDao;
import org.eurekaclinical.phenotype.service.dao.JpaThresholdsOperatorDao;
import org.eurekaclinical.phenotype.service.dao.JpaTimeUnitDao;
import org.eurekaclinical.phenotype.service.dao.JpaValueComparatorDao;
import org.eurekaclinical.phenotype.service.dao.PhenotypeEntityDao;
import org.eurekaclinical.phenotype.service.dao.RelationOperatorDao;
import org.eurekaclinical.phenotype.service.dao.ThresholdsOperatorDao;
import org.eurekaclinical.phenotype.service.dao.TimeUnitDao;
import org.eurekaclinical.phenotype.service.dao.ValueComparatorDao;
import org.eurekaclinical.phenotype.service.finder.PropositionFinder;
import org.eurekaclinical.phenotype.service.finder.SystemPropositionFinder;


import org.eurekaclinical.phenotype.service.dao.JpaRoleDao;
import org.eurekaclinical.phenotype.service.dao.JpaUserDao;

import org.eurekaclinical.standardapis.entity.RoleEntity;
import org.eurekaclinical.standardapis.entity.UserEntity;
import org.eurekaclinical.phenotype.service.dao.RoleDao;
import org.eurekaclinical.standardapis.dao.UserDao;

/**
 * Created by akalsan on 10/4/16.
 */
public class AppModule extends AbstractModule {

        @Override
        protected void configure() {
                bind(new TypeLiteral<UserDao<? extends UserEntity<? extends RoleEntity>>>() {}).to(JpaUserDao.class);
                bind(AuthorizedUserDao.class).to(JpaUserDao.class);
                bind(RoleDao.class).to(JpaRoleDao.class);

                bind(PhenotypeEntityDao.class).to(JpaPhenotypeEntityDao.class);
                bind(TimeUnitDao.class).to(JpaTimeUnitDao.class);
                bind(RelationOperatorDao.class).to(JpaRelationOperatorDao.class);
                bind(ValueComparatorDao.class).to(JpaValueComparatorDao.class);
                bind(ThresholdsOperatorDao.class).to(JpaThresholdsOperatorDao.class);
                bind(FrequencyTypeDao.class).to(JpaFrequencyTypeDao.class);
                bind(ThresholdsOperatorDao.class).to
                                (JpaThresholdsOperatorDao.class);  
                bind(new TypeLiteral<PropositionFinder<
                                String>>(){}).to(SystemPropositionFinder.class);

                bind(Context.class).to(InitialContext.class);                
        }
}
