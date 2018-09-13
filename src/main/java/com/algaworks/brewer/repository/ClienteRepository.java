package com.algaworks.brewer.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.algaworks.brewer.model.Cliente;
import com.algaworks.brewer.repository.helper.ClienteQueries;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long>, ClienteQueries {

	public Optional<Cliente> findByCpfCnpjOrId(String cpfCnpj, Long id);

	public List<Cliente> findByNomeStartingWithIgnoreCase(String nome);
}
