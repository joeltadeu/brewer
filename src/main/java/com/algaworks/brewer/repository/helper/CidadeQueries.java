package com.algaworks.brewer.repository.helper;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.algaworks.brewer.model.Cidade;
import com.algaworks.brewer.repository.filter.CidadeFilter;

public interface CidadeQueries {

	public Page<Cidade> filtrar(CidadeFilter filtro, Pageable pageable);
}
