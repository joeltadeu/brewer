package com.algaworks.brewer.service;

import java.util.Optional;

import javax.persistence.PersistenceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.algaworks.brewer.model.Cidade;
import com.algaworks.brewer.repository.CidadeRepository;
import com.algaworks.brewer.service.exception.ImpossivelExcluirEntidadeException;
import com.algaworks.brewer.service.exception.NomeCidadeJaCadastradaException;

@Service
public class CidadeService {

	@Autowired
	private CidadeRepository cidadeRepository;

	@Transactional
	public void salvar(Cidade cidade) {
		Optional<Cidade> cidadeExistente = cidadeRepository.findByNomeAndEstado(cidade.getNome(), cidade.getEstado());

		if (cidadeExistente.isPresent()) {
			throw new NomeCidadeJaCadastradaException("{cidade.business.cidade.ja.cadastrada}");
		}
		cidadeRepository.save(cidade);
	}
	
	@Transactional
	public void excluir(Cidade cidade) {
		try {
			cidadeRepository.delete(cidade);
		} catch (PersistenceException e) {
			throw new ImpossivelExcluirEntidadeException("{cidade.business.cidade.informada.por.outros.clientes}");
		}
	}
}
