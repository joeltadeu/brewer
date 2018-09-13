package com.algaworks.brewer.service;

import com.algaworks.brewer.repository.UsuarioRepository;

public enum StatusUsuario {
	ATIVAR {

		@Override
		public void executar(Long[] ids, UsuarioRepository usuarioRepository) {
			usuarioRepository.findByIdIn(ids).forEach(u -> u.setAtivo(true));
		}

	},
	DESATIVAR {
		@Override
		public void executar(Long[] ids, UsuarioRepository usuarioRepository) {
			usuarioRepository.findByIdIn(ids).forEach(u -> u.setAtivo(false));
		}
	};

	public abstract void executar(Long[] ids, UsuarioRepository usuarioRepository);
}
