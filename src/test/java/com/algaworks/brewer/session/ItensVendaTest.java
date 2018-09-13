package com.algaworks.brewer.session;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;

import org.junit.Before;
import org.junit.Test;

import com.algaworks.brewer.model.Cerveja;
import com.algaworks.brewer.session.ItensVenda;

public class ItensVendaTest {
	
	private ItensVenda itensVenda;
	
	@Before
	public void setup() {
		this.itensVenda = new ItensVenda("1");
	}
	
	@Test
	public void calcularValorTotalSemItens() throws Exception {
		assertEquals(BigDecimal.ZERO, itensVenda.getValorTotal());
	}
	
	@Test
	public void calcularValorTotalComUmItem() throws Exception {
		Cerveja cerveja = new Cerveja();
		BigDecimal valor = new BigDecimal("8.90");
		cerveja.setValor(valor);
		
		itensVenda.adicionar(cerveja, 1);
		
		assertEquals(valor, itensVenda.getValorTotal());
		
	}
	
	@Test
	public void manterTamanhoListaMesmasCervejas() throws Exception {
		Cerveja c1 = new Cerveja();
		c1.setId(1L);
		c1.setValor(new BigDecimal("8.90"));
		
		Cerveja c2 = new Cerveja();
		c2.setId(1L);
		c2.setValor(new BigDecimal("8.90"));
		
		itensVenda.adicionar(c1, 1);
		itensVenda.adicionar(c2, 1);
		
		assertEquals(1, itensVenda.total());
		assertEquals(new BigDecimal("17.80"), itensVenda.getValorTotal());
	}
	
	@Test
	public void calcularValorTotalComVariosItens() throws Exception {
	
		Cerveja c1 = new Cerveja();
		BigDecimal v1 = new BigDecimal("8.90");
		c1.setId(1L);
		c1.setValor(v1);
		
		Cerveja c2 = new Cerveja();
		BigDecimal v2 = new BigDecimal("4.99");
		c2.setId(2L);
		c2.setValor(v2);
		
		itensVenda.adicionar(c1, 1);
		itensVenda.adicionar(c2, 2);
		
		assertEquals(new BigDecimal("18.88"), itensVenda.getValorTotal());
		
	}
	
	@Test
	public void alterarQuantidadeItem() throws Exception {
		Cerveja c1 = new Cerveja();
		c1.setId(1L);
		c1.setValor(new BigDecimal("8.90"));
		
		itensVenda.adicionar(c1, 1);
		itensVenda.alterarQuantidade(c1, 4);
		
		assertEquals(1, itensVenda.total());
		assertEquals(new BigDecimal("35.60"), itensVenda.getValorTotal());
	}
	
	@Test
	public void removerItem() throws Exception {
		Cerveja c1 = new Cerveja();
		c1.setId(1L);
		c1.setValor(new BigDecimal("8.90"));
		
		Cerveja c2 = new Cerveja();
		c2.setId(2L);
		c2.setValor(new BigDecimal("4.99"));
		
		Cerveja c3 = new Cerveja();
		c3.setId(3L);
		c3.setValor(new BigDecimal("2.00"));
		
		itensVenda.adicionar(c1, 1);
		itensVenda.adicionar(c2, 2);
		itensVenda.adicionar(c3, 1);
		
		itensVenda.remover(c2);
		
		assertEquals(2, itensVenda.total());
		assertEquals(new BigDecimal("10.90"), itensVenda.getValorTotal());
	}
}
