package com.algaworks.brewer.repository.helper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.MonthDay;
import java.time.Year;
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
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.SingularAttribute;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.hibernate.query.criteria.internal.path.SingularAttributeJoin;
import org.hibernate.sql.JoinType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.algaworks.brewer.dto.VendaMes;
import com.algaworks.brewer.dto.VendaOrigem;
import com.algaworks.brewer.model.Cliente;
import com.algaworks.brewer.model.StatusVenda;
import com.algaworks.brewer.model.Venda;
import com.algaworks.brewer.repository.filter.VendaFilter;
import com.algaworks.brewer.repository.pagination.PaginationUtil;

public class VendaRepositoryImpl implements VendaQueries {

	@PersistenceContext
	private EntityManager manager;

	@Autowired
	private PaginationUtil<Venda> paginationUtil;

	@Override
	@Transactional(readOnly = true)
	public Page<Venda> filtrar(VendaFilter filtro, Pageable pageable) {
		List<Predicate> predicates = new ArrayList<Predicate>();
		CriteriaBuilder builder = manager.unwrap(Session.class).getCriteriaBuilder();
		CriteriaQuery<Venda> criteria = builder.createQuery(Venda.class);
		Root<Venda> root = criteria.from(Venda.class);

		generateFilter(filtro, predicates, builder, root);

		paginationUtil.prepareSort(pageable, builder, criteria, root);
		paginationUtil.prepareWhere(criteria, root, predicates);

		TypedQuery<Venda> typedQuery = manager.unwrap(Session.class).createQuery(criteria);
		paginationUtil.preparePagination(pageable, typedQuery);

		return new PageImpl<>(typedQuery.getResultList(), pageable, total(filtro));
	}

	@Override
	@SuppressWarnings("deprecation")
	@Transactional(readOnly = true)
	public Venda buscarComItens(Long id) {
		Criteria criteria = manager.unwrap(Session.class).createCriteria(Venda.class);
		criteria.createAlias("itens", "it", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq("id", id));
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		return (Venda) criteria.uniqueResult();
	}

	@Override
	public BigDecimal valorTotalNoAno() {
		Optional<BigDecimal> optional = Optional.ofNullable(manager
				.createQuery("select sum(valorTotal) from Venda where year(dataCriacao) = :ano and status = :status", BigDecimal.class)
				.setParameter("ano", Year.now().getValue())
				.setParameter("status", StatusVenda.EMITIDA).getSingleResult());
		return optional.orElse(BigDecimal.ZERO);
	}

	@Override
	public BigDecimal valorTotalNoMes() {
		Optional<BigDecimal> optional = Optional.ofNullable(manager
				.createQuery("select sum(valorTotal) from Venda where year(dataCriacao) = :ano and month(dataCriacao) = :mes and status = :status", BigDecimal.class)
				.setParameter("ano", Year.now().getValue())
				.setParameter("mes", MonthDay.now().getMonthValue())
				.setParameter("status", StatusVenda.EMITIDA).getSingleResult());
		return optional.orElse(BigDecimal.ZERO);
	}
	
	@Override
	public BigDecimal valorTicketMedioNoAno() {
		Optional<BigDecimal> optional = Optional.ofNullable(manager
				.createQuery("select sum(valorTotal)/count(*) from Venda where year(dataCriacao) = :ano and status = :status", BigDecimal.class)
				.setParameter("ano", Year.now().getValue())
				.setParameter("status", StatusVenda.EMITIDA).getSingleResult());
		return optional.orElse(BigDecimal.ZERO);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<VendaMes> totalPorMes() {
		List<VendaMes> vendas = manager.createNamedQuery("VendaRepository.totalPorMes").getResultList();
		
		LocalDate today = LocalDate.now();
		for (int i=1; i <=6; i++) {
			String mesIdeal = String.format("%d/%02d", today.getYear(), today.getMonthValue());
			
			boolean possuiMes = vendas.stream().filter(v -> v.getMes().equals(mesIdeal)).findAny().isPresent();
			if (!possuiMes) {
				vendas.add(i-1, new VendaMes(mesIdeal, 0));
			}
			
			today = today.minusMonths(1);
		}
		
		return vendas;
	}
	
	@Override
	public List<VendaOrigem> totalPorOrigem() {
		List<VendaOrigem> vendas = manager.createNamedQuery("VendaRepository.totalPorOrigem", VendaOrigem.class).getResultList();
		
		LocalDate today = LocalDate.now();
		for (int i=1; i <=6; i++) {
			String mesIdeal = String.format("%d/%02d", today.getYear(), today.getMonthValue());
			
			boolean possuiMes = vendas.stream().filter(v -> v.getMes().equals(mesIdeal)).findAny().isPresent();
			if (!possuiMes) {
				vendas.add(i-1, new VendaOrigem(mesIdeal, 0, 0));
			}
			
			today = today.minusMonths(1);
		}
		
		return vendas;
	}
	
	@SuppressWarnings("unchecked")
	private void generateFilter(VendaFilter filtro, List<Predicate> predicates, CriteriaBuilder builder,
			Root<Venda> root) {

		EntityType<Venda> Venda_ = root.getModel();

		SingularAttributeJoin<Venda, Cliente> joinCliente = (SingularAttributeJoin<Venda, Cliente>) root
				.join((SingularAttribute<? super Venda, Cliente>) Venda_.getAttribute("cliente"));

		if (filtro != null) {

			if (filtro.getStatus() != null) {
				predicates.add(builder.equal(root.get("status"), filtro.getStatus()));
			}

			if (!StringUtils.isEmpty(filtro.getNomeCliente())) {
				predicates.add(builder.like(joinCliente.get("nome"), "%" + filtro.getNomeCliente() + "%"));
			}

			if (!StringUtils.isEmpty(filtro.getCpfOuCnpj())) {
				predicates.add(builder.equal(joinCliente.get("cpfOuCnpj"), filtro.getCpfOuCnpj()));
			}

			if (filtro.getDesde() != null) {
				LocalDateTime desde = LocalDateTime.of(filtro.getDesde(), LocalTime.of(0, 0));
				predicates.add(builder.greaterThanOrEqualTo(root.get("dataCriacao"), desde));
			}

			if (filtro.getAte() != null) {
				LocalDateTime ate = LocalDateTime.of(filtro.getDesde(), LocalTime.of(0, 0));
				predicates.add(builder.lessThanOrEqualTo(root.get("dataCriacao"), ate));
			}

			if (filtro.getValorMinimo() != null) {
				predicates.add(builder.ge(root.get("valorTotal"), filtro.getValorMinimo()));
			}

			if (filtro.getValorMaximo() != null) {
				predicates.add(builder.le(root.get("valorTotal"), filtro.getValorMaximo()));
			}
		}
	}

	private Long total(VendaFilter filtro) {

		CriteriaBuilder builder = manager.unwrap(Session.class).getCriteriaBuilder();
		CriteriaQuery<Object> query = builder.createQuery();

		Root<Venda> root = query.from(Venda.class);
		List<Predicate> predicates = new ArrayList<Predicate>();

		generateFilter(filtro, predicates, builder, root);
		query.select(builder.count(root)).where(predicates.toArray(new Predicate[] {}));
		TypedQuery<Object> q = manager.unwrap(Session.class).createQuery(query);
		return (Long) q.getSingleResult();
	}

	
}
