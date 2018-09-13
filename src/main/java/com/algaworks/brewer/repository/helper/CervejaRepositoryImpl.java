package com.algaworks.brewer.repository.helper;

import java.math.BigDecimal;
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

import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.algaworks.brewer.dto.CervejaDTO;
import com.algaworks.brewer.model.Cerveja;
import com.algaworks.brewer.repository.filter.CervejaFilter;
import com.algaworks.brewer.repository.pagination.PaginationUtil;
import com.algaworks.brewer.storage.FotoStorage;

public class CervejaRepositoryImpl implements CervejaQueries {

	@PersistenceContext
	private EntityManager manager;

	@Autowired
	private PaginationUtil<Cerveja> paginationUtil;
	
	@Autowired
	private FotoStorage fotoStorage;

	@Override
	@Transactional(readOnly = true)
	public Page<Cerveja> filtrar(CervejaFilter filtro, Pageable pageable) {
		List<Predicate> predicates = new ArrayList<Predicate>();
		CriteriaBuilder builder = manager.unwrap(Session.class).getCriteriaBuilder();
		CriteriaQuery<Cerveja> criteria = builder.createQuery(Cerveja.class);
		Root<Cerveja> root = criteria.from(Cerveja.class);
		criteria.select(root);

		generateCervejaFilter(filtro, predicates, builder, root);

		paginationUtil.prepareSort(pageable, builder, criteria, root);
		paginationUtil.prepareWhere(criteria, root, predicates);

		TypedQuery<Cerveja> typedQuery = manager.unwrap(Session.class).createQuery(criteria);
		paginationUtil.preparePagination(pageable, typedQuery);

		return new PageImpl<>(typedQuery.getResultList(), pageable, total(filtro));
	}

	private void generateCervejaFilter(CervejaFilter filtro, List<Predicate> predicates, CriteriaBuilder builder,
			Root<Cerveja> cervejaRoot) {
		if (filtro != null) {
			if (!StringUtils.isEmpty(filtro.getSku())) {
				predicates.add(builder.equal(cervejaRoot.get("sku"), filtro.getSku()));
			}

			if (temEstilo(filtro)) {
				predicates.add(builder.equal(cervejaRoot.get("estilo"), filtro.getEstilo()));
			}

			if (!StringUtils.isEmpty(filtro.getNome())) {
				predicates.add(builder.like(cervejaRoot.get("nome"), "%" + filtro.getNome() + "%"));
			}

			if (filtro.getSabor() != null) {
				predicates.add(builder.equal(cervejaRoot.get("sabor"), filtro.getSabor()));
			}

			if (filtro.getOrigem() != null) {
				predicates.add(builder.equal(cervejaRoot.get("origem"), filtro.getOrigem()));
			}

			if (filtro.getValorDe() != null) {
				predicates.add(builder.ge(cervejaRoot.get("valor"), filtro.getValorDe()));
			}

			if (filtro.getValorAte() != null) {
				predicates.add(builder.le(cervejaRoot.get("valor"), filtro.getValorAte()));
			}
		}
	}

	private Long total(CervejaFilter filtro) {

		CriteriaBuilder builder = manager.unwrap(Session.class).getCriteriaBuilder();
		CriteriaQuery<Object> query = builder.createQuery();

		Root<Cerveja> root = query.from(Cerveja.class);
		List<Predicate> predicates = new ArrayList<Predicate>();

		generateCervejaFilter(filtro, predicates, builder, root);
		query.select(builder.count(root)).where(predicates.toArray(new Predicate[] {}));
		TypedQuery<Object> q = manager.unwrap(Session.class).createQuery(query);
		return (Long) q.getSingleResult();
	}

	private boolean temEstilo(CervejaFilter filtro) {
		return filtro.getEstilo() != null && filtro.getEstilo().getId() != null;
	}

	@Override
	public List<CervejaDTO> porSkuOuNome(String skuOuNome) {
		String jpql = "select new com.algaworks.brewer.dto.CervejaDTO(id, sku, nome, origem, valor, foto) "
				+ "from Cerveja where lower(sku) like lower(:skuOuNome) or lower(nome) like lower(:skuOuNome)";
		List<CervejaDTO> cervejas = manager.createQuery(jpql, CervejaDTO.class).setParameter("skuOuNome", skuOuNome + "%")
				.getResultList();
		
		cervejas.forEach(c -> c.setUrlThumbnailFoto(fotoStorage.getUrl(FotoStorage.THUMBNAIL_PREFIX + c.getFoto())));
		
		return cervejas;
	}
	
	@Override
	public BigDecimal valorTotalEstoque() {
		Optional<BigDecimal> optional = Optional.ofNullable(manager
				.createQuery("select sum(valor * quantidadeEstoque) from Cerveja", BigDecimal.class)
				.getSingleResult());
		return optional.orElse(BigDecimal.ZERO);
	}
	
	@Override
	public Long totalItensEstoque() {
		Optional<Long> optional = Optional
				.ofNullable(manager.createQuery("select sum(quantidadeEstoque) from Cerveja", Long.class).getSingleResult());
		return optional.orElse(Long.valueOf(0));
	}
}
