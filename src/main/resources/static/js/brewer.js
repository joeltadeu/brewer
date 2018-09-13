var Brewer = Brewer || {};

Brewer.MaskMoney = (function() {

	function MaskMoney() {
		this.decimal = $('.js-decimal');
		this.integer = $('.js-integer');
	}

	MaskMoney.prototype.enable = function() {
		this.decimal.maskNumber({
			decimal : ',',
			thousands : '.'
		});

		this.integer.maskNumber({
			integer : true,
			thousands : '.'
		});
	}

	return MaskMoney;

}());
numeral.locale('pt-br');
Brewer.currencyFormat = function(valor) {
	return numeral(valor).format('0,0.00');
}

Brewer.currencyValue = function(valorFormatado) {
	return numeral().unformat(valorFormatado);
}

Brewer.MaskPhoneNumber = (function() {
	function MaskPhoneNumber() {
		this.phone = $('.js-phone');
	}
	
	MaskPhoneNumber.prototype.enable = function() {
		var maskBehavior = function (val) {
		    return val.replace(/\D/g, '').length === 11 ? '(00) 00000-0000' : '(00) 0000-00009';
		  };
		  
		  var spOptions = {
		    onKeyPress: function(val, e, field, options) {
		        field.mask(maskBehavior.apply({}, arguments), options);
		      }
		  };

		  this.phone.mask(maskBehavior, spOptions);
	}
	
	return MaskPhoneNumber;
}());


Brewer.MaskDate = (function() {
	function MaskDate() {
		this.inputDate = $('.js-date');
	}
	
	MaskDate.prototype.enable = function() {
		this.inputDate.mask('00/00/0000');
		this.inputDate.datepicker({
			orientation: 'bottom',
			language: 'pt-BR',
			autoclose:  true
				
		});
	}
	
	return MaskDate;
}());

Brewer.Security = (function() {
	function Security() {
		this.token = $('input[name=_csrf]').val();
		this.header = $('input[name=_csrf_header]').val();
	}
	
	Security.prototype.enable = function() {
		$(document).ajaxSend(function (event, jqxhr, settings) {
			jqxhr.setRequestHeader(this.header, this.token);
		}.bind(this));
	}
	
	return Security;
}());

$(function() {
	var maskMoney = new Brewer.MaskMoney();
	maskMoney.enable();
	
	var maskPhoneNumber = new Brewer.MaskPhoneNumber();
	maskPhoneNumber.enable();
	
	var maskDate = new Brewer.MaskDate();
	maskDate.enable();
	
	var security = new Brewer.Security();
	security.enable();
});
