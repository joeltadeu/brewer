package com.algaworks.brewer.repository.helper;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.algaworks.brewer.model.Usuario;
import com.algaworks.brewer.repository.filter.UsuarioFilter;

public interface UsuarioQueries {

	public Page<Usuario> filtrar(UsuarioFilter filtro, Pageable pageable);

	public Optional<Usuario> porEmailAtivo(String email);

	public List<String> permissoes(Usuario usuario);
	
	public Usuario buscarComGrupos(Long id);
}
