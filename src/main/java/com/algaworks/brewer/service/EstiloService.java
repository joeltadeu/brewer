package com.algaworks.brewer.service;

import java.util.Optional;

import javax.persistence.PersistenceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.algaworks.brewer.model.Estilo;
import com.algaworks.brewer.repository.EstiloRepository;
import com.algaworks.brewer.service.exception.ImpossivelExcluirEntidadeException;
import com.algaworks.brewer.service.exception.NomeEstiloJaCadastradoException;

@Service
public class EstiloService {

	@Autowired
	private EstiloRepository estiloRepository;

	@Transactional
	public Estilo salvar(Estilo estilo) {

		Optional<Estilo> estiloExiste = estiloRepository.findByNomeIgnoreCase(estilo.getNome());
		if (estiloExiste.isPresent()) {
			throw new NomeEstiloJaCadastradoException("{estilo.business.nome.estilo.ja.cadastrado}");
		}
		return estiloRepository.saveAndFlush(estilo);
	}
	
	@Transactional
	public void excluir(Estilo estilo) {
		try {
			estiloRepository.delete(estilo);
		} catch (PersistenceException e) {
			throw new ImpossivelExcluirEntidadeException("{estilo.business.estilo.utilizado.por.outras.cervejas}");
		}
	}
}
