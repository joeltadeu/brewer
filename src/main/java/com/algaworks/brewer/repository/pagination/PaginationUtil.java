package com.algaworks.brewer.repository.pagination;

import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component
public class PaginationUtil<T> {

	public void prepare(Criteria criteria, Pageable pageable) {
		int paginaAtual = pageable.getPageNumber();
		int totalRegistrosPorPagina = pageable.getPageSize();
		int primeiroRegistro = paginaAtual * totalRegistrosPorPagina;
		
		criteria.setFirstResult(primeiroRegistro);
		criteria.setMaxResults(totalRegistrosPorPagina);
		
		Sort sort = pageable.getSort();
		if (sort != null) {
			Sort.Order order = sort.iterator().next();
			String property = order.getProperty();
			criteria.addOrder(order.isAscending() ? Order.asc(property) : Order.desc(property));
		}
	}
	
	public void prepareWhere(CriteriaQuery<T> criteria, Root<T> root, List<Predicate> predicates) {
		criteria.select(root).where(predicates.toArray(new Predicate[] {}));
	}

	public void prepareSort(Pageable pageable, CriteriaBuilder builder, CriteriaQuery<T> criteria, Root<T> root) {
		Sort sort = pageable.getSort();
		if (sort != null) {
			org.springframework.data.domain.Sort.Order order = sort.iterator().next();
			String field = order.getProperty();
			criteria.orderBy(order.isAscending() ? builder.asc(root.get(field)) : builder.desc(root.get(field)));
		}
	}

	public void preparePagination(Pageable pageable, TypedQuery<T> typedQuery) {
		int totalRegistrosPorPagina = pageable.getPageSize();
		int paginaAtual = pageable.getPageNumber();
		int primeiroRegistro = paginaAtual * totalRegistrosPorPagina;

		typedQuery.setFirstResult(primeiroRegistro);
		typedQuery.setMaxResults(totalRegistrosPorPagina);
	}
}
