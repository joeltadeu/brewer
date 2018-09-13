Brewer = Brewer || {}

Brewer.BotaoSubmit = (function() {

	function BotaoSubmit() {
		this.submitBtn = $('.js-submit-btn');
		this.form = $('.js-main-form');
	}

	BotaoSubmit.prototype.iniciar = function() {
		this.submitBtn.on('click', onSubmit.bind(this));
	}
	
	function onSubmit(event) {
		event.preventDefault();
		var botaoClicado = $(event.target);
		var acao = botaoClicado.data('acao');
		
		var acaoInput = $('<input>');
		acaoInput.attr('name', acao);
		
		this.form.append(acaoInput);
		this.form.submit();
	}

	return BotaoSubmit;

}());

$(function() {
	var botaoSubmit = new Brewer.BotaoSubmit();
	botaoSubmit.iniciar();
});