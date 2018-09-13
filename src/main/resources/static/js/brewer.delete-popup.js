Brewer  = Brewer || {};

Brewer.DeletePopup = (function() {
	
	function DeletePopup() {
		this.exclusaoBtn = $('.js-delete-btn');	
	}
	
	DeletePopup.prototype.iniciar = function() {
		this.exclusaoBtn.on('click', onDelete.bind(this));
		
		if (window.location.search.indexOf('excluido') > -1) {
			swal('Pronto!', 'Excluído com sucesso', 'success');
		}
	}
	
	function onDelete(event) {
		event.preventDefault();
		var btnClick = $(event.currentTarget);
		var url = btnClick.data('url');
		var modelName = btnClick.data('model-nome');
		
		swal({
			title: 'Tem certeza?',
			text: 'Excluir "' + modelName + '"? Você não poderá recuperar depois.',
			showCancelButton: true,
			confirmButtonColor: '#DD6B55',
			confirmButtonText: 'Sim, exclua agora!',
			closeOnConfirm: false
		}, onDeleteConfirmed.bind(this, url));
	}
	
	function onDeleteConfirmed(url) {
		$.ajax({
			url: url,
			method: 'DELETE',
			success: onDeleteSuccess.bind(this),
			error: onDeleteError.bind(this)
		});
	}
	
	function onDeleteSuccess() {
		var urlAtual = window.location.href;
		var separador = urlAtual.indexOf('?') > -1 ? '&' : '?';
		var novaUrl = urlAtual.indexOf('excluido') > -1 ? urlAtual : urlAtual + separador + 'excluido';
		window.location = novaUrl;
		
	}
	
	function onDeleteError(e) {
		swal('Oops!', e.responseText, 'error');
	}
	
	return DeletePopup;
}());

$(function() {
	var popup = new Brewer.DeletePopup();
	popup.iniciar();
});