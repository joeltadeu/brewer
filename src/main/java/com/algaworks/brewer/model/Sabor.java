package com.algaworks.brewer.model;

public enum Sabor {
	ADOCICADA("Adocicada"), 
	AMARGA("Amarga"), 
	FORTE("Forte"), 
	FRUTADA("Frutada");

	private String descricao;

	Sabor(String descricao) {
		this.descricao = descricao;
	}

	public String getDescricao() {
		return descricao;
	}
}
