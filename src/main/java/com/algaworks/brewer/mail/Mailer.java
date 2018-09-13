package com.algaworks.brewer.mail;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.Locale;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.algaworks.brewer.controller.CervejaController;
import com.algaworks.brewer.model.ItemVenda;
import com.algaworks.brewer.model.Venda;
import com.algaworks.brewer.storage.FotoStorage;

@Component
public class Mailer {

	private static final Logger logger = LoggerFactory.getLogger(CervejaController.class);

	@Autowired
	private Environment env;

	@Autowired
	private JavaMailSender mailSender;

	@Autowired
	private TemplateEngine thymeleaf;

	@Autowired
	private FotoStorage fotoStorage;

	@Async
	public void enviar(Venda venda) {
		Context context = new Context(new Locale("pt", "BR"));
		context.setVariable("venda", venda);
		context.setVariable("logo", "logo");
		String html = thymeleaf.process("mail/resumo-venda", context);

		adicionarContextFotoVariable(venda, context);

		try {
			MimeMessage mimeMessage = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
			helper.setFrom(env.getProperty("email.Default.SMTP.Login"));
			helper.setTo(venda.getCliente().getEmail());
			helper.setSubject(String.format("Brewer - Venda n° %d", venda.getId()));
			helper.setText(html, true);
			helper.addInline("logo", new ClassPathResource("static/images/logo-gray.png"));

			adicionarContextFoto(venda, helper);

			mailSender.send(mimeMessage);
		} catch (MessagingException | IOException e) {
			logger.error("Erro enviando e-mail", e);
		}
	}

	private void adicionarContextFoto(Venda venda, MimeMessageHelper helper) throws MessagingException, IOException {
		
		for (ItemVenda item : venda.getItens()) {
			byte[] foto = fotoStorage.recuperarThumbnail(item.getCerveja().getFotoOrMock());
			InputStream is = new BufferedInputStream(new ByteArrayInputStream(foto));
			helper.addInline("foto-" + item.getCerveja().getId(), new ByteArrayResource(foto), URLConnection.guessContentTypeFromStream(is));
		}
	}

	private void adicionarContextFotoVariable(Venda venda, Context context) {
		for (ItemVenda item : venda.getItens()) {
			context.setVariable("foto-" + item.getCerveja().getId(), "foto-" + item.getCerveja().getId());
		}
	}
}
