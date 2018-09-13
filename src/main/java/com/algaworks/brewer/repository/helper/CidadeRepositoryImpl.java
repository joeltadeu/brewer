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

import com.algaworks.brewer.model.Cidade;
import com.algaworks.brewer.repository.filter.CidadeFilter;
import com.algaworks.brewer.repository.pagination.PaginationUtil;

public class CidadeRepositoryImpl implements CidadeQueries {

	@PersistenceContext
	private EntityManager manager;

	@Autowired
	private PaginationUtil<Cidade> paginationUtil;

	@Override
	@Transactional(readOnly = true)
	public Page<Cidade> filtrar(CidadeFilter filtro, Pageable pageable) {
		List<Predicate> predicates = new ArrayList<Predicate>();
		CriteriaBuilder builder = manager.unwrap(Session.class).getCriteriaBuilder();
		CriteriaQuery<Cidade> criteria = builder.createQuery(Cidade.class);
		Root<Cidade> root = criteria.from(Cidade.class);
		criteria.select(root);

		generateCidadeFilter(filtro, predicates, builder, root);

		paginationUtil.prepareSort(pageable, builder, criteria, root);
		paginationUtil.prepareWhere(criteria, root, predicates);

		TypedQuery<Cidade> typedQuery = manager.unwrap(Session.class).createQuery(criteria);
		paginationUtil.preparePagination(pageable, typedQuery);

		return new PageImpl<>(typedQuery.getResultList(), pageable, total(filtro));
	}

	private void generateCidadeFilter(CidadeFilter filtro, List<Predicate> predicates, CriteriaBuilder builder,
			Root<Cidade> root) {
		if (filtro != null) {

			if (!StringUtils.isEmpty(filtro.getNome())) {
				predicates.add(builder.like(root.get("nome"), "%" + filtro.getNome() + "%"));
			}

			if (temEstado(filtro)) {
				predicates.add(builder.equal(root.get("estado"), filtro.getEstado()));
			}
		}
	}

	private boolean temEstado(CidadeFilter filtro) {
		return filtro.getEstado() != null && filtro.getEstado().getId() != null;
	}

	private Long total(CidadeFilter filtro) {

		CriteriaBuilder builder = manager.unwrap(Session.class).getCriteriaBuilder();
		CriteriaQuery<Object> query = builder.createQuery();

		Root<Cidade> root = query.from(Cidade.class);
		List<Predicate> predicates = new ArrayList<Predicate>();

		generateCidadeFilter(filtro, predicates, builder, root);
		query.select(builder.count(root)).where(predicates.toArray(new Predicate[] {}));
		TypedQuery<Object> q = manager.unwrap(Session.class).createQuery(query);
		return (Long) q.getSingleResult();
	}
}
