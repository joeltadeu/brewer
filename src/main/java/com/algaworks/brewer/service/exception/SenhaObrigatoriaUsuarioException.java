package com.algaworks.brewer.service.exception;

public class SenhaObrigatoriaUsuarioException extends RuntimeException {

	private static final long serialVersionUID = 2827529923081059797L;

	public SenhaObrigatoriaUsuarioException(String message) {
		super(message);
	}
}
