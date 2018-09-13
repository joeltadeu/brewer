package com.algaworks.brewer.repository.helper;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.algaworks.brewer.model.Estilo;
import com.algaworks.brewer.repository.filter.EstiloFilter;
import com.algaworks.brewer.repository.pagination.PaginationUtil;

public class EstiloRepositoryImpl implements EstiloQueries {

	@PersistenceContext
	private EntityManager manager;

	@Autowired
	private PaginationUtil<Estilo> paginationUtil;

	@Override
	@Transactional(readOnly = true)
	public Page<Estilo> filtrar(EstiloFilter filtro, Pageable pageable) {
		List<Predicate> predicates = new ArrayList<Predicate>();
		CriteriaBuilder builder = manager.unwrap(Session.class).getCriteriaBuilder();
		CriteriaQuery<Estilo> criteria = builder.createQuery(Estilo.class);
		Root<Estilo> root = criteria.from(Estilo.class);
		criteria.select(root);

		generateEstiloFilter(filtro, predicates, builder, root);

		paginationUtil.prepareSort(pageable, builder, criteria, root);
		paginationUtil.prepareWhere(criteria, root, predicates);

		TypedQuery<Estilo> typedQuery = manager.unwrap(Session.class).createQuery(criteria);
		paginationUtil.preparePagination(pageable, typedQuery);
		
		return new PageImpl<>(typedQuery.getResultList(), pageable, total(filtro));
	}

	private void generateEstiloFilter(EstiloFilter filtro, List<Predicate> predicates, CriteriaBuilder builder,
			Root<Estilo> root) {
		if (filtro != null) {

			if (!StringUtils.isEmpty(filtro.getNome())) {
				predicates.add(builder.like(root.get("nome"), "%" + filtro.getNome() + "%"));
			}
		}
	}

	private Long total(EstiloFilter filtro) {

		CriteriaBuilder builder = manager.unwrap(Session.class).getCriteriaBuilder();
		CriteriaQuery<Object> query = builder.createQuery();

		Root<Estilo> root = query.from(Estilo.class);
		List<Predicate> predicates = new ArrayList<Predicate>();

		generateEstiloFilter(filtro, predicates, builder, root);
		query.select(builder.count(root)).where(predicates.toArray(new Predicate[] {}));
		TypedQuery<Object> q = manager.unwrap(Session.class).createQuery(query);
		return (Long) q.getSingleResult();
	}
}
