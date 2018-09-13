package com.algaworks.brewer.controller;

import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.algaworks.brewer.controller.page.PageWrapper;
import com.algaworks.brewer.controller.validator.VendaValidator;
import com.algaworks.brewer.dto.VendaMes;
import com.algaworks.brewer.dto.VendaOrigem;
import com.algaworks.brewer.mail.Mailer;
import com.algaworks.brewer.model.Cerveja;
import com.algaworks.brewer.model.ItemVenda;
import com.algaworks.brewer.model.StatusVenda;
import com.algaworks.brewer.model.TipoPessoa;
import com.algaworks.brewer.model.Venda;
import com.algaworks.brewer.repository.CervejaRepository;
import com.algaworks.brewer.repository.VendaRepository;
import com.algaworks.brewer.repository.filter.VendaFilter;
import com.algaworks.brewer.security.UsuarioSistema;
import com.algaworks.brewer.service.VendaService;
import com.algaworks.brewer.session.ItensSession;

@Controller
@RequestMapping("/vendas")
public class VendasController {

	@Autowired
	private CervejaRepository cervejaRepository;

	@Autowired
	private VendaRepository vendaRepository;

	@Autowired
	private ItensSession itens;

	@Autowired
	private VendaService vendaService;

	@Autowired
	private VendaValidator vendaValidator;

	@Autowired
	private Mailer mailer;

	@GetMapping("/nova")
	public ModelAndView nova(Venda venda) {
		ModelAndView mv = new ModelAndView("venda/form");

		setUuid(venda);

		mv.addObject("itens", venda.getItens());
		mv.addObject("valorFrete", venda.getValorFrete());
		mv.addObject("valorDesconto", venda.getValorDesconto());
		mv.addObject("valorTotalItens", itens.getValorTotal(venda.getUuid()));

		return mv;
	}

	@PostMapping(value = "/nova", params = "salvar")
	public ModelAndView salvar(Venda venda, BindingResult result, RedirectAttributes attributes,
			@AuthenticationPrincipal UsuarioSistema usuarioSistema) {

		validarVenda(venda, result, usuarioSistema);

		if (result.hasErrors()) {
			return nova(venda);
		}

		vendaService.salvar(venda);
		attributes.addFlashAttribute("mensagem", "{venda.message.salva.com.sucesso}");
		return new ModelAndView("redirect:/vendas/nova");
	}

	@GetMapping("/totalPorMes")
	public @ResponseBody List<VendaMes> listarTotalVendaPorMes() {
		return vendaRepository.totalPorMes();
	}
	
	@GetMapping("/totalPorOrigem")
	public @ResponseBody List<VendaOrigem> listarTotalVendaPorOrigem() {
		return vendaRepository.totalPorOrigem();
	}

	@PostMapping(value = "/nova", params = "emitir")
	public ModelAndView emitir(Venda venda, BindingResult result, RedirectAttributes attributes,
			@AuthenticationPrincipal UsuarioSistema usuarioSistema) {

		validarVenda(venda, result, usuarioSistema);

		if (result.hasErrors()) {
			return nova(venda);
		}

		vendaService.emitir(venda);
		attributes.addFlashAttribute("mensagem", "{venda.message.emitida.com.sucesso}");
		return new ModelAndView("redirect:/vendas/nova");
	}

	@PostMapping(value = "/nova", params = "enviarEmail")
	public ModelAndView enviarEmail(Venda venda, BindingResult result, RedirectAttributes attributes,
			@AuthenticationPrincipal UsuarioSistema usuarioSistema) {

		validarVenda(venda, result, usuarioSistema);

		if (result.hasErrors()) {
			return nova(venda);
		}

		venda = vendaService.salvar(venda);
		mailer.enviar(venda);

		attributes.addFlashAttribute("mensagem",
				String.format("{venda.message.salva.com.sucesso.envio.email}", venda.getId()));
		return new ModelAndView("redirect:/vendas/nova");
	}

	@PostMapping(value = "/nova", params = "cancelar")
	public ModelAndView cancelar(Venda venda, BindingResult result, RedirectAttributes attributes,
			@AuthenticationPrincipal UsuarioSistema usuarioSistema) {

		try {
			vendaService.cancelar(venda);
		} catch (AccessDeniedException e) {
			return new ModelAndView("/403");
		}

		attributes.addFlashAttribute("mensagem", "{venda.message.cancelada.com.sucesso}");
		return new ModelAndView("redirect:/vendas/" + venda.getId());
	}

	@GetMapping
	public ModelAndView pesquisar(VendaFilter vendaFilter, @PageableDefault(size = 10) Pageable pageable,
			HttpServletRequest httpServletRequest) {
		ModelAndView mv = new ModelAndView("/venda/search");
		mv.addObject("statusVenda", StatusVenda.values());
		mv.addObject("tipoPessoa", TipoPessoa.values());

		PageWrapper<Venda> paginaWrapper = new PageWrapper<>(vendaRepository.filtrar(vendaFilter, pageable),
				httpServletRequest);
		mv.addObject("pagina", paginaWrapper);

		return mv;
	}

	@PostMapping("/item")
	public ModelAndView adicionarItem(Long id, String uuid) {
		Cerveja cerveja = cervejaRepository.findOne(id);
		itens.adicionar(uuid, cerveja, 1);
		return mvItensVenda(uuid);
	}

	@PutMapping("/item/{id}")
	public ModelAndView alterarQuantidadeItem(@PathVariable("id") Cerveja cerveja, Integer quantidade, String uuid) {
		itens.alterarQuantidade(uuid, cerveja, quantidade);
		return mvItensVenda(uuid);
	}

	@DeleteMapping("/item/{uuid}/{id}")
	public ModelAndView removerItem(@PathVariable("id") Cerveja cerveja, @PathVariable("uuid") String uuid) {
		itens.remover(uuid, cerveja);
		return mvItensVenda(uuid);
	}

	@GetMapping("/{id}")
	public ModelAndView editar(@PathVariable Long id) {
		Venda venda = vendaRepository.buscarComItens(id);

		setUuid(venda);
		for (ItemVenda item : venda.getItens()) {
			itens.adicionar(venda.getUuid(), item.getCerveja(), item.getQuantidade());
		}

		ModelAndView mv = nova(venda);
		mv.addObject(venda);

		return mv;
	}

	private void setUuid(Venda venda) {
		if (StringUtils.isEmpty(venda.getUuid())) {
			venda.setUuid(UUID.randomUUID().toString());
		}
	}

	private ModelAndView mvItensVenda(String uuid) {
		ModelAndView mv = new ModelAndView("venda/itens-venda");
		mv.addObject("itens", itens.getItens(uuid));
		mv.addObject("valorTotal", itens.getValorTotal(uuid));
		return mv;
	}

	private void validarVenda(Venda venda, BindingResult result, UsuarioSistema usuarioSistema) {
		venda.adicionarItens(itens.getItens(venda.getUuid()));
		venda.calcularValorTotal();
		venda.setUsuario(usuarioSistema.getUsuario());

		vendaValidator.validate(venda, result);
	}
}