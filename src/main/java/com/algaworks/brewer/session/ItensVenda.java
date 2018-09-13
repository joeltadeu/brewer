package com.algaworks.brewer.session;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import com.algaworks.brewer.model.Cerveja;
import com.algaworks.brewer.model.ItemVenda;

class ItensVenda {

	private String uuid;
	private List<ItemVenda> itens = new ArrayList<>();

	public ItensVenda(String uuid) {
		this.uuid = uuid;
	}
	
	
	public String getUuid() {
		return uuid;
	}

	public BigDecimal getValorTotal() {
		return itens.stream().map(ItemVenda::getValorTotal).reduce(BigDecimal::add).orElse(BigDecimal.ZERO);
	}

	public void adicionar(Cerveja cerveja, Integer quantidade) {
		Optional<ItemVenda> itemVendaOptional = buscarItemPorCerveja(cerveja);
		
		ItemVenda item = null;
		
		if (itemVendaOptional.isPresent()) {
			item = itemVendaOptional.get();
			item.setQuantidade(item.getQuantidade() + quantidade);
		} else {
			item = new ItemVenda();
			item.setCerveja(cerveja);
			item.setQuantidade(quantidade);
			item.setValorUnitario(cerveja.getValor());
			itens.add(0, item);
		}
	}

	private Optional<ItemVenda> buscarItemPorCerveja(Cerveja cerveja) {
		Optional<ItemVenda> itemVendaOptional = itens.stream().filter(i -> i.getCerveja().equals(cerveja)).findAny();
		return itemVendaOptional;
	}

	public void alterarQuantidade(Cerveja cerveja, Integer quantidade) {
		ItemVenda itemVenda = buscarItemPorCerveja(cerveja).get();
		itemVenda.setQuantidade(quantidade);
	}
	
	public void remover(Cerveja cerveja) {
		int index = IntStream.range(0, itens.size()).filter(i -> itens.get(i).getCerveja().equals(cerveja)).findAny().getAsInt();
		itens.remove(index);
	}
	
	public int total() {
		return itens.size();
	}

	public List<ItemVenda> getItens() {
		return itens;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ItensVenda other = (ItensVenda) obj;
		if (uuid == null) {
			if (other.uuid != null)
				return false;
		} else if (!uuid.equals(other.uuid))
			return false;
		return true;
	}
}
