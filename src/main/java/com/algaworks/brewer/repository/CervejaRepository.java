package com.algaworks.brewer.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.algaworks.brewer.model.Cerveja;
import com.algaworks.brewer.repository.helper.CervejaQueries;

@Repository
public interface CervejaRepository extends JpaRepository<Cerveja, Long>, CervejaQueries {

	public Optional<Cerveja> findBySku(String sku);
}
