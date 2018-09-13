Brewer = Brewer || {};

Brewer.PesquisaRapidaCliente = (function() {
	
	function PesquisaRapidaCliente() {
		this.pesquisaRapidaClientesModal = $('#pesquisaRapidaClientes');
		this.nomeInput = $('#nomeClienteModal');
		this.pesquisaRapidaBtn = $('.js-pesquisa-rapida-clientes-btn');
		this.containerTabelaPesquisa = $('#containerTabelaPesquisaRapidaClientes');
		this.htmlTabelaPesquisa = $('#tabela-pesquisa-cliente-modal').html();
		this.template = Handlebars.compile(this.htmlTabelaPesquisa);
		this.mensagemErro = $('.js-mensagem-erro');
	}
	
	PesquisaRapidaCliente.prototype.iniciar = function() {
		this.pesquisaRapidaClientesModal.on('shown.bs.modal', onModalShow.bind(this));
		this.pesquisaRapidaClientesModal.on('hide.bs.modal', onModalClose.bind(this));
		this.pesquisaRapidaBtn.on('click', onPesquisaRapidaClicado.bind(this));
	}
	
	function onModalShow() {
		this.nomeInput.focus();
	}
	
	function onModalClose() {
		this.nomeInput.val('');
		this.containerTabelaPesquisa.html('');
		this.mensagemErro.addClass('hidden');
	}
	
	function onPesquisaRapidaClicado(event) {
		event.preventDefault();
		
		$.ajax({
			url: this.pesquisaRapidaClientesModal.find('form').attr('action'),
			method: 'GET',
			contentType: 'application/json',
			data: {
				nome: this.nomeInput.val()
			}, 
			success: onPesquisaConcluida.bind(this),
			error: onErroPesquisa.bind(this)
		});
	}
	
	function onPesquisaConcluida(resultado) {
		var html = this.template(resultado);
		this.containerTabelaPesquisa.html(html);
		this.mensagemErro.addClass('hidden');
		
		var tabelaClientePesquisaModal = new Brewer.TabelaClienteModal(this.pesquisaRapidaClientesModal);
		tabelaClientePesquisaModal.iniciar();
	}
	
	function onErroPesquisa() {
		this.mensagemErro.removeClass('hidden');
	}
	
	return PesquisaRapidaCliente;
	
}());

Brewer.TabelaClienteModal = (function() {
	
	function TabelaClienteModal(modal) {
		this.modalCliente = modal;
		this.cliente = $('.js-cliente-pesquisa-modal');
	}
	
	TabelaClienteModal.prototype.iniciar = function() {
		this.cliente.on('click', onClienteSelecionado.bind(this));
	}
	
	function onClienteSelecionado(evento) {
		var clienteSelecionado = $(evento.currentTarget);
		this.modalCliente.modal('hide');
		$('#nomeCliente').val(clienteSelecionado.data('nome'));
		$('#idCliente').val(clienteSelecionado.data('id'));
		
	}
	
	return TabelaClienteModal;
}());

$(function() {
	var pesquisaRapidaCliente = new Brewer.PesquisaRapidaCliente();
	pesquisaRapidaCliente.iniciar();
})