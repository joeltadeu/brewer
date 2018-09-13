Brewer.Venda = (function() {
	function Venda(itensVenda) {
		this.itensVenda = itensVenda;
		this.valorTotalBox = $('.js-valor-total-box');
		this.valorFreteInput = $('#valorFrete');
		this.valorDescontoInput = $('#valorDesconto');
		this.valorTotalBoxContainer = $('.js-valor-total-box-container');
		
		this.valorTotalItens = this.itensVenda.valorTotal();
		this.valorFrete = this.valorFreteInput.data('valor');
		this.valorDesconto = this.valorDescontoInput.data('valor');
		
	}
	
	Venda.prototype.iniciar = function() {
		this.itensVenda.on('itens-atualizados', onItensAtualizados.bind(this));
		this.valorFreteInput.on('keyup', onValorFreteAlterado.bind(this));
		this.valorDescontoInput.on('keyup', onValorDescontoAlterado.bind(this));
		
		this.itensVenda.on('itens-atualizados', onValoresAlterados.bind(this));
		this.valorFreteInput.on('keyup', onValoresAlterados.bind(this));
		this.valorDescontoInput.on('keyup', onValoresAlterados.bind(this));
		
		onValoresAlterados.call(this);
	}
	
	function onValorFreteAlterado(evento) {
		this.valorFrete = numeral($(evento.target).val()).value();
	}
	
	function onValorDescontoAlterado(evento) {
		this.valorDesconto = numeral($(evento.target).val()).value();
	}
	
	function onValoresAlterados() {
		var valorTotal = numeral(parseFloat(this.valorTotalItens)).value() + numeral(parseFloat(this.valorFrete)).value() - numeral(parseFloat(this.valorDesconto)).value();
		this.valorTotalBox.html(Brewer.currencyFormat(valorTotal));
		this.valorTotalBoxContainer.toggleClass('negativo', valorTotal < 0);
	}
	
	function onItensAtualizados(evento, valorTotalItens) {
		this.valorTotalItens = valorTotalItens == null ? 0 : valorTotalItens;
	}
	
	return Venda;
	
}());

$(function() {
	
	var autocomplete = new Brewer.Autocomplete();
	autocomplete.iniciar();
	
	var itensVenda = new Brewer.ItensVenda(autocomplete);
	itensVenda.iniciar();
	
	var venda = new Brewer.Venda(itensVenda);
	venda.iniciar();
});