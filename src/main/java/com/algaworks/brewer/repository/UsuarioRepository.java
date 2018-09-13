package com.algaworks.brewer.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.algaworks.brewer.model.Usuario;
import com.algaworks.brewer.repository.helper.UsuarioQueries;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long>, UsuarioQueries {

	public Optional<Usuario> findByEmail(String email);

	public List<Usuario> findByIdIn(Long[] ids);

	public Optional<Usuario> findByEmailOrId(String email, Long id);
}
