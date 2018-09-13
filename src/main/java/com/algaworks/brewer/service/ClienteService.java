package com.algaworks.brewer.service;

import java.util.Optional;

import javax.persistence.PersistenceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.algaworks.brewer.model.Cliente;
import com.algaworks.brewer.repository.ClienteRepository;
import com.algaworks.brewer.service.exception.CpfCnpjClienteJaCadastradoException;
import com.algaworks.brewer.service.exception.ImpossivelExcluirEntidadeException;

@Service
public class ClienteService {

	@Autowired
	private ClienteRepository clienteRepository;

	@Transactional
	public void salvar(Cliente cliente) {
		Optional<Cliente> clienteExistente = clienteRepository.findByCpfCnpjOrId(cliente.getCpfCnpjSemFormatacao(),
				cliente.getId());

		if (clienteExistente.isPresent() && !clienteExistente.get().equals(cliente)) {
			throw new CpfCnpjClienteJaCadastradoException("{cliente.business.cpfCnpj.ja.cadastrado}");
		}
		clienteRepository.save(cliente);
	}

	@Transactional
	public void excluir(Cliente cliente) {
		try {
			clienteRepository.delete(cliente);
		} catch (PersistenceException e) {
			throw new ImpossivelExcluirEntidadeException("{cliente.business.cliente.realizou.outras.venda}");
		}
	}
}
