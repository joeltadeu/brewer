package com.algaworks.brewer.service;

import java.util.Optional;

import javax.persistence.PersistenceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.algaworks.brewer.model.Usuario;
import com.algaworks.brewer.repository.UsuarioRepository;
import com.algaworks.brewer.service.exception.EmailUsuarioJaCadastradoException;
import com.algaworks.brewer.service.exception.ImpossivelExcluirEntidadeException;
import com.algaworks.brewer.service.exception.SenhaObrigatoriaUsuarioException;

@Service
public class UsuarioService {

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Transactional
	public void salvar(Usuario usuario) {
		Optional<Usuario> usuarioExistente = usuarioRepository.findByEmailOrId(usuario.getEmail(), usuario.getId());
		if (usuarioExistente.isPresent() && !usuarioExistente.get().equals(usuario)) {
			throw new EmailUsuarioJaCadastradoException("{usuario.business.email.ja.cadastrado}");
		}
			
		if (usuario.isNew() && StringUtils.isEmpty(usuario.getSenha())) {
			throw new SenhaObrigatoriaUsuarioException("{usuario.business.senha.obrigatoria}");
		}
			
		if (usuario.isNew() || !StringUtils.isEmpty(usuario.getSenha())) {
			usuario.setSenha(this.passwordEncoder.encode(usuario.getSenha()));
		} else if (StringUtils.isEmpty(usuario.getSenha())) {
			Usuario u = usuarioRepository.findOne(usuario.getId());
			usuario.setSenha(u.getSenha());
		}
		usuario.setConfirmacaoSenha(usuario.getSenha());
			
		if (!usuario.isNew() && usuario.getAtivo() == null) {
			Usuario u = usuarioRepository.findOne(usuario.getId());
			usuario.setAtivo(u.getAtivo());
		}
		
		usuarioRepository.save(usuario);
	}

	@Transactional
	public void alterarStatus(Long[] ids, StatusUsuario statusUsuario) {
		statusUsuario.executar(ids, usuarioRepository);
	}

	@Transactional
	public void excluir(Usuario usuario) {
		try {
			usuarioRepository.delete(usuario);
		} catch (PersistenceException e) {
			throw new ImpossivelExcluirEntidadeException("{usuario.business.usuario.responsavel.outras.vendas}");
		}
	}
}
