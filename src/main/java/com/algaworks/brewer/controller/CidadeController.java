package com.algaworks.brewer.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.algaworks.brewer.controller.page.PageWrapper;
import com.algaworks.brewer.model.Cidade;
import com.algaworks.brewer.repository.CidadeRepository;
import com.algaworks.brewer.repository.EstadoRepository;
import com.algaworks.brewer.repository.filter.CidadeFilter;
import com.algaworks.brewer.service.CidadeService;
import com.algaworks.brewer.service.exception.ImpossivelExcluirEntidadeException;
import com.algaworks.brewer.service.exception.NomeCidadeJaCadastradaException;

@Controller
@RequestMapping("/cidades")
public class CidadeController {

	@Autowired
	EstadoRepository estadoRepository;

	@Autowired
	CidadeRepository cidadeRepository;

	@Autowired
	CidadeService cidadeService;

	@RequestMapping("/novo")
	public ModelAndView novo(Cidade cidade) {
		ModelAndView mv = new ModelAndView("cidade/form");
		mv.addObject("estados", estadoRepository.findAll());
		return mv;
	}

	@Secured("CADASTRAR_CIDADE")
	@RequestMapping(value = { "/novo", "{\\d+}" }, method = RequestMethod.POST)
	@CacheEvict(value = "cidades", key = "#cidade.estado.id", condition = "#cidade.temEstado()")
	public ModelAndView salvar(@Valid Cidade cidade, BindingResult result, Model model, RedirectAttributes attributes) {

		if (result.hasErrors()) {
			return novo(cidade);
		}

		try {
			cidadeService.salvar(cidade);
		} catch (NomeCidadeJaCadastradaException e) {
			result.rejectValue("nome", e.getMessage(), e.getMessage());
			return novo(cidade);
		}

		attributes.addFlashAttribute("mensagem", "{cidade.message.salva.com.sucesso}");
		return new ModelAndView("redirect:/cidades/novo");
	}

	@Cacheable(value = "cidades", key = "#id")
	@RequestMapping(value = "/cidadesPorEstado", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE, headers = "content-type=application/x-www-form-urlencoded")
	public @ResponseBody List<Cidade> pesquisarPorCodigoEstado(
			@RequestParam(name = "estado", defaultValue = "-1") Long id) {
		return cidadeRepository.findByEstadoId(id);
	}

	@GetMapping()
	public ModelAndView pesquisar(CidadeFilter cidadeFilter, BindingResult result,
			@PageableDefault(size = 10) Pageable pageable, HttpServletRequest httpServletRequest) {
		ModelAndView mv = new ModelAndView("cidade/search");
		PageWrapper<Cidade> paginaWrapper = new PageWrapper<>(cidadeRepository.filtrar(cidadeFilter, pageable),
				httpServletRequest);
		mv.addObject("pagina", paginaWrapper);
		mv.addObject("estados", estadoRepository.findAll());
		return mv;
	}
	
	@DeleteMapping("/{id}")
	public @ResponseBody ResponseEntity<?> excluir(@PathVariable("id") Cidade cidade) {
		try {
			cidadeService.excluir(cidade);
		} catch (ImpossivelExcluirEntidadeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}

		return ResponseEntity.ok().build();
	}

	@GetMapping("/{id}")
	public ModelAndView editar(@PathVariable("id") Cidade cidade) {
		cidade = cidadeRepository.findOne(cidade.getId());
		ModelAndView mv = novo(cidade);
		mv.addObject(cidade);

		return mv;
	}
}
