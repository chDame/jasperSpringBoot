/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at

 * http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.example.service;

import java.util.List;
import java.util.Optional;

import org.example.model.BaseEntity;
import org.example.repository.AbstractRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.jpa.domain.Specification;

/**
 * @author christophe.dame
 */
public abstract class AbstractService<T extends BaseEntity<Long>> {

	protected abstract AbstractRepository<T> getRepository();
	
	private JpaSearchLuceneBuilder<T> jpaSearchLuceneBuilder = new JpaSearchLuceneBuilder<>();
	
	public Specification<T> getSpecifications(String luceneQuery) {
		return jpaSearchLuceneBuilder.getJpaSpecificationFromQuery(luceneQuery);
	}
	
	private Pageable getPageable(int count, int page, String orderBy, String order) {
		return PageRequest.of(page - 1, count, Direction.valueOf(order), orderBy);
	}

	public List<T> list(String query, int count, int page, String orderBy, String order) {
		Pageable pageable = getPageable(count, page, orderBy, order);
		return getRepository().findAll(getSpecifications(query), pageable).getContent();
	}
	
	public List<T> list(Specification<T> query, int count, int page, String orderBy, String order) {
		Pageable pageable = getPageable(count, page, orderBy, order);
		return getRepository().findAll(query, pageable).getContent();
	}

    public T getById(Long id) {
        Optional<T> entity = getRepository().findById(id);
        if (entity.isPresent()) {
        	return entity.get();
        }
        return null;
    }
	
	public T create(T object) {
		return getRepository().save(object);
	}

	public T update(T u) {
		return getRepository().save(u);
	}
	
	public boolean delete(Long id) {
        getRepository().deleteById(id);
        return true;
    }

	public Long count() {
		return count(null);
	}
	
	public Long count(Specification<T> query) {
		if (query==null) {
			return getRepository().count();
		}
		return getRepository().count(query);
	}

	public void deleteAll() {
		getRepository().deleteAll();
	}
	
}
