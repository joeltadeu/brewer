package com.algaworks.brewer.repository.helper;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.algaworks.brewer.model.Cliente;
import com.algaworks.brewer.repository.filter.ClienteFilter;

public interface ClienteQueries {

	public Page<Cliente> filtrar(ClienteFilter filtro, Pageable pageable);
	
	public Cliente buscarClienteComCidadeEstado(Long id);
	
	public Long totalClientes();
}
