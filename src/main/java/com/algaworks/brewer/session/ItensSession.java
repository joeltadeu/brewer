package com.algaworks.brewer.session;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import com.algaworks.brewer.model.Cerveja;
import com.algaworks.brewer.model.ItemVenda;

@SessionScope
@Component
public class ItensSession {
	private Set<ItensVenda> itens = new HashSet<>();

	public void adicionar(String uuid, Cerveja cerveja, int quantidade) {
		ItensVenda itensVenda = buscarItemPorUuId(uuid);
		itensVenda.adicionar(cerveja, quantidade);
		itens.add(itensVenda);
	}

	public void alterarQuantidade(String uuid, Cerveja cerveja, Integer quantidade) {
		ItensVenda itensVenda = buscarItemPorUuId(uuid);
		itensVenda.alterarQuantidade(cerveja, quantidade);
	}

	public void remover(String uuid, Cerveja cerveja) {
		ItensVenda itensVenda = buscarItemPorUuId(uuid);
		itensVenda.remover(cerveja);
	}

	public List<ItemVenda> getItens(String uuid) {
		ItensVenda itensVenda = buscarItemPorUuId(uuid);
		return itensVenda.getItens();
	}
	
	public Object getValorTotal(String uuid) {
		ItensVenda itensVenda = buscarItemPorUuId(uuid);
		return itensVenda.getValorTotal();
	}
	
	private ItensVenda buscarItemPorUuId(String uuid) {
		ItensVenda itensVenda = itens.stream()
				.filter(t -> t.getUuid().equals(uuid))
				.findAny()
				.orElse(new ItensVenda(uuid));
		return itensVenda;
	}
}
