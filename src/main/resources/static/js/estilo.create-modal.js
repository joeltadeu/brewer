var Brewer = Brewer || {};

Brewer.SalvarEstiloModal = (function() {
	
	function SalvarEstiloModal() {
		this.windowModal = $('#modalEstilo');
		this.btnSalvar = this.windowModal.find('.js-modal-estilo-btn');
		this.form = this.windowModal.find('form');
		this.inputEstilo = $('#nomeEstilo');
		this.divMensagemErro = $('.js-mensagem-estilo-modal');
		this.url = this.form.attr('action');
	}
	
	SalvarEstiloModal.prototype.iniciar = function() {
		this.form.on('submit', function(event) { event.preventDefault()	});
		this.windowModal.on('shown.bs.modal', onModalShow.bind(this));
		this.windowModal.on('hide.bs.modal', onModalClose.bind(this));
		this.btnSalvar.on('click', onSalvarClick.bind(this));
	}
	
	function onModalShow() {
		this.inputEstilo.focus();
	}
	
	function onModalClose() {
		this.inputEstilo.val('');
		this.divMensagemErro.addClass('hidden');
		this.form.find('.form-group').removeClass('has-error');
	}
	
	function onSalvarClick() {
		var nomeEstilo = this.inputEstilo.val().trim();
		$.ajax({
			url : this.url,
			method : 'POST',
			contentType : 'application/JSON',
			data : JSON.stringify({
				nome : nomeEstilo
			}),
			error : onErroSalvarEstilo.bind(this),
			success : onSuccessSalvarEstilo.bind(this)
		})
	}

	function onErroSalvarEstilo(obj) {
		var mensagemErro = obj.responseText;
		this.divMensagemErro.removeClass('hidden');
		this.divMensagemErro.html('<span>' + mensagemErro + '</span>');
		this.form.find('.form-group').addClass('has-error');
	}

	function onSuccessSalvarEstilo(estilo) {
		var comboEstilo = $('#estilo');
		comboEstilo.append('<option value=' + estilo.id + '>' + estilo.nome	+ '</option>');
		comboEstilo.val(estilo.id);
		this.windowModal.modal('hide');
	}
	
	return SalvarEstiloModal;
	
}());


$(function() {
	var estiloModal = new Brewer.SalvarEstiloModal();
	estiloModal.iniciar();
});