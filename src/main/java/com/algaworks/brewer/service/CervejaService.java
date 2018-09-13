package com.algaworks.brewer.service;

import javax.persistence.PersistenceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.algaworks.brewer.model.Cerveja;
import com.algaworks.brewer.repository.CervejaRepository;
import com.algaworks.brewer.service.exception.ImpossivelExcluirEntidadeException;
import com.algaworks.brewer.storage.FotoStorage;

@Service
public class CervejaService {

	@Autowired
	private CervejaRepository cervejaRepository;

	@Autowired
	private FotoStorage fotoStorage;
	
	@Transactional
	public void salvar(Cerveja cerveja) {
		cervejaRepository.save(cerveja);
	}
	
	@Transactional
	public void excluir(Cerveja cerveja) {
		try {
			String foto = cerveja.getFoto();
			cervejaRepository.delete(cerveja);
			cervejaRepository.flush();
			fotoStorage.delete(foto);
		} catch (PersistenceException e) {
			throw new ImpossivelExcluirEntidadeException("{cerveja.business.cerveja.ja.utilizada.outra.venda}");
		}
		
	}
}
