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
package org.eurekaclinical.phenotype.service.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Provider;
import org.eurekaclinical.phenotype.service.entity.PhenotypeEntity_;
import org.eurekaclinical.phenotype.service.entity.PhenotypeEntity;

import org.eurekaclinical.standardapis.dao.GenericDao;

/**
 * An implementation of the {@link PhenotypeEntityDao} interface, backed by
 * JPA entities and queries.
 *
 * @author hrathod
 */
public class JpaPhenotypeEntityDao extends GenericDao<PhenotypeEntity, Long>
		implements PhenotypeEntityDao {

	private static final Logger LOGGER
			= LoggerFactory.getLogger(JpaPhenotypeEntityDao.class);

	/**
	 * Create an object with the given entity manager provider.
	 *
	 * @param inProvider An entity manager provider.
	 */
	@Inject
	public JpaPhenotypeEntityDao(Provider<EntityManager> inProvider) {
		super(PhenotypeEntity.class, inProvider);
	}

	@Override
	public PhenotypeEntity getByUserAndKey(Long inUserId, String inKey) {
		return getByUserAndKey(inUserId, inKey, true);
	}

	@Override
	public PhenotypeEntity getUserOrSystemByUserAndKey(Long inUserId, String inKey) {
		return getByUserAndKey(inUserId, inKey, false);
	}

	@Override
	public List<PhenotypeEntity> getByUserId(Long inUserId) {
		EntityManager entityManager = this.getEntityManager();
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<PhenotypeEntity> criteriaQuery = builder.createQuery(PhenotypeEntity.class);
		Root<PhenotypeEntity> root = criteriaQuery.from(PhenotypeEntity.class);
		Predicate userPredicate = builder.equal(
				root.get(
						PhenotypeEntity_.userId), inUserId);
		Predicate notInSystemPredicate = builder.equal(root.get(PhenotypeEntity_.inSystem), false);
		TypedQuery<PhenotypeEntity> typedQuery = entityManager.createQuery(
				criteriaQuery.where(
						builder.and(userPredicate, notInSystemPredicate)));
		return typedQuery.getResultList();
	}

	@Override
	public PhenotypeEntity getById(Long inId) {
		PhenotypeEntity result = null;
            
		EntityManager entityManager = this.getEntityManager();
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<PhenotypeEntity> criteriaQuery = builder.createQuery(PhenotypeEntity.class);
		Root<PhenotypeEntity> root = criteriaQuery.from(PhenotypeEntity.class);
                
		Predicate idPredicate = builder.equal(
				root.get(
						PhenotypeEntity_.id), inId);
		
		TypedQuery<PhenotypeEntity> typedQuery = entityManager.createQuery(
				criteriaQuery.where(
						builder.and(idPredicate)));
		result = typedQuery.getSingleResult();
		return result;
	}        
        
	private PhenotypeEntity getByUserAndKey(Long inUserId, String inKey, boolean excludeSystemElements) {
		PhenotypeEntity result = null;
                
		EntityManager entityManager = this.getEntityManager();
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<PhenotypeEntity> criteriaQuery = builder.createQuery(PhenotypeEntity.class);
		Root<PhenotypeEntity> root = criteriaQuery.from(PhenotypeEntity.class);
		
		List<Predicate> predicates = new ArrayList<>(3);
		Predicate userPredicate = builder.equal(
				root.get(
						PhenotypeEntity_.userId), inUserId);
		predicates.add(userPredicate);
		Predicate keyPredicate = builder.equal(root.get(PhenotypeEntity_.key),
				inKey);
		predicates.add(keyPredicate);
		if (excludeSystemElements) {
			Predicate notInSystemPredicate = builder.equal(root.get(PhenotypeEntity_.inSystem),
					false);
			predicates.add(notInSystemPredicate);
		}
		
		TypedQuery<PhenotypeEntity> typedQuery = entityManager.createQuery(
					criteriaQuery.where(
							builder.and(predicates.toArray(
									new Predicate[predicates.size()]))));

		try {
			result = typedQuery.getSingleResult();
		} catch (NonUniqueResultException nure) {
			LOGGER.warn("Result not unique for user id = {} and key = {}",
					inUserId, inKey);
		} catch (NoResultException nre) {
		}

		return result;
	}
}
