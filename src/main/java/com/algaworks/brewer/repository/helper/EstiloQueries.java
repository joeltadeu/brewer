package com.algaworks.brewer.repository.helper;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.algaworks.brewer.model.Estilo;
import com.algaworks.brewer.repository.filter.EstiloFilter;

public interface EstiloQueries {

	public Page<Estilo> filtrar(EstiloFilter filtro, Pageable pageable);
}
