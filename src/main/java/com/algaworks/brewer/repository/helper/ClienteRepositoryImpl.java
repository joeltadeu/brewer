package com.algaworks.brewer.repository.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.algaworks.brewer.model.Cliente;
import com.algaworks.brewer.repository.filter.ClienteFilter;
import com.algaworks.brewer.repository.pagination.PaginationUtil;

public class ClienteRepositoryImpl implements ClienteQueries {

	@PersistenceContext
	private EntityManager manager;

	@Autowired
	private PaginationUtil<Cliente> paginationUtil;

	@Override
	@Transactional(readOnly = true)
	public Page<Cliente> filtrar(ClienteFilter filtro, Pageable pageable) {
		List<Predicate> predicates = new ArrayList<Predicate>();
		CriteriaBuilder builder = manager.unwrap(Session.class).getCriteriaBuilder();
		CriteriaQuery<Cliente> criteria = builder.createQuery(Cliente.class);
		Root<Cliente> root = criteria.from(Cliente.class);
		criteria.select(root);

		generateClienteFilter(filtro, predicates, builder, root);

		paginationUtil.prepareSort(pageable, builder, criteria, root);
		paginationUtil.prepareWhere(criteria, root, predicates);

		TypedQuery<Cliente> typedQuery = manager.unwrap(Session.class).createQuery(criteria);
		paginationUtil.preparePagination(pageable, typedQuery);

		return new PageImpl<>(typedQuery.getResultList(), pageable, total(filtro));
	}

	private void generateClienteFilter(ClienteFilter filtro, List<Predicate> predicates, CriteriaBuilder builder,
			Root<Cliente> clienteRoot) {
		if (filtro != null) {

			if (!StringUtils.isEmpty(filtro.getNome())) {
				predicates.add(builder.like(clienteRoot.get("nome"), "%" + filtro.getNome() + "%"));
			}

			if (!StringUtils.isEmpty(filtro.getCpfCnpj())) {
				predicates.add(builder.equal(clienteRoot.get("cpfCnpj"), filtro.getCpfCnpj()));
			}
		}
	}

	private Long total(ClienteFilter filtro) {

		CriteriaBuilder builder = manager.unwrap(Session.class).getCriteriaBuilder();
		CriteriaQuery<Object> query = builder.createQuery();

		Root<Cliente> root = query.from(Cliente.class);
		List<Predicate> predicates = new ArrayList<Predicate>();

		generateClienteFilter(filtro, predicates, builder, root);
		query.select(builder.count(root)).where(predicates.toArray(new Predicate[] {}));
		TypedQuery<Object> q = manager.unwrap(Session.class).createQuery(query);
		return (Long) q.getSingleResult();
	}

	@SuppressWarnings("deprecation")
	@Override
	@Transactional(readOnly = true)
	public Cliente buscarClienteComCidadeEstado(Long id) {
		Criteria criteria = manager.unwrap(Session.class).createCriteria(Cliente.class);
		criteria.createAlias("endereco.cidade", "c", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("c.estado", "e", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq("id", id));
		return (Cliente) criteria.uniqueResult();
	}

	@Override
	public Long totalClientes() {
		Optional<Long> optional = Optional
				.ofNullable(manager.createQuery("select count(id) from Cliente", Long.class).getSingleResult());
		return optional.orElse(Long.valueOf(0));
	}
}
