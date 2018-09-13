package com.algaworks.brewer.controller.validator;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.algaworks.brewer.model.Venda;

@Component
public class VendaValidator implements Validator {

	@Override
	public boolean supports(Class<?> clazz) {
		return Venda.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		ValidationUtils.rejectIfEmpty(errors, "cliente.id", "", "{venda.validacao.selecione.cliente}");

		Venda venda = (Venda) target;
		validarHorarioEntrega(errors, venda);
		validarExisteItens(errors, venda);
		validarValorTotalNegativo(errors, venda);
	}

	private void validarValorTotalNegativo(Errors errors, Venda venda) {
		if (venda.getValorTotal().compareTo(BigDecimal.ZERO) < 0) {
			errors.reject("", "{venda.validacao.valor.nao.pode.ser.negativo}");
		}
	}

	private void validarExisteItens(Errors errors, Venda venda) {
		if (venda.getItens().isEmpty()) {
			errors.reject("", "{venda.validacao.cerveja.quantidade.minima.selecionada}");
		}
	}

	private void validarHorarioEntrega(Errors errors, Venda venda) {
		if (venda.getHorarioEntrega() != null && venda.getDataEntrega() == null) {
			errors.rejectValue("dataEntrega", "", "{venda.validacao.dataEntrega.obrigatorio}");
		}
	}
}
