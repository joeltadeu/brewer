Brewer.ItensVenda = (function() {
	
	function ItensVenda(autocomplete) {
		this.autocomplete = autocomplete;
		this.container = $('.js-itens-venda-container');
		this.uuid = $('#uuid').val();
		this.emitter = $({});
		this.on = this.emitter.on.bind(this.emitter);
	}
	
	ItensVenda.prototype.iniciar = function() {
		this.autocomplete.on('item-selecionado', onItemSelecionado.bind(this));
		bindQuantidade.call(this);
		bindTabelaItensVenda.call(this);
	}
	
	ItensVenda.prototype.valorTotal = function() {
		return this.container.data('valor');
	}
	
	function onItemSelecionado(evento, item) {
		var resposta = $.ajax({
			url: 'item',
			method: 'POST',
			data: {
				id: item.id,
				uuid: this.uuid
			}
		});
		
		resposta.done(onItemAtualizado.bind(this));
	}
	
	function onItemAtualizado(html) {
		this.container.html(html);
		
		bindQuantidade.call(this);
		var tabelaItensVenda = bindTabelaItensVenda.call(this); 
		this.emitter.trigger('itens-atualizados', tabelaItensVenda.data('valor-total'));
	}

	function bindTabelaItensVenda() {
		var tabelaItensVenda = $('.js-tabela-item');
		tabelaItensVenda.on('dblclick', onDoubleClick);
		$('.js-exclusao-item-btn').on('click', onExclusaoItemClick.bind(this));
		return tabelaItensVenda;
	}
	function bindQuantidade() {
		var quantidadeItemInput = $('.js-cerveja-quantidade-item');
		quantidadeItemInput.on('change', onQuantidadeItemAlterado.bind(this));
		quantidadeItemInput.maskNumber({integer : true, thousand: ''});
	}
	
	function onDoubleClick(evento) {
		//var item = $(evento.currentTarget);
		$(this).toggleClass('solicitando-exclusao');
	}
	
	function onExclusaoItemClick(evento) {
		var cervejaId =  $(evento.target).data('cerveja-id');
		var resposta = $.ajax({
			url: 'item/' + this.uuid + '/' + cervejaId,
			method: 'DELETE',
			data: {
				id: cervejaId,
				uuid: this.uuid
				
			}
		});
		
		resposta.done(onItemAtualizado.bind(this));
	}
	
	function onQuantidadeItemAlterado(evento) {
		var input = $(evento.target);
		var quantidade = input.val();
		var cervejaId = input.data('cerveja-id');
		
		var resposta = $.ajax({
			url: 'item/' + cervejaId,
			method: 'PUT',
			data: {
				quantidade: quantidade,
				uuid: this.uuid
			}
		});
		
		resposta.done(onItemAtualizado.bind(this));
	}
	
	return ItensVenda;
	
}());