package com.algaworks.brewer.config;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
@ComponentScan("com.algaworks.brewer.mail")
@PropertySources({
	@PropertySource({ "classpath:env/mail-${envTarget:desenv}.properties" }),
	@PropertySource({ "file:\\${USERPROFILE}\\.brewer-mail.properties" })
})
public class MailConfig {
	
	@Autowired
	private Environment env;
	
	@Bean
	public JavaMailSender mailSender() {
		JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setHost(env.getProperty("smtp.host"));
		mailSender.setPort(587);
		mailSender.setUsername(env.getProperty("email.username"));
		mailSender.setPassword(env.getProperty("email.password"));

		System.out.println(">> Username: " + env.getProperty("email.username"));
		System.out.println(">> Password: " + env.getProperty("email.password"));
		
		Properties properties = new Properties();
		properties.put("mail.transport.protocol", "smtp");
		properties.put("mail.smtp.auth", true);
		properties.put("mail.smtp.starttls.enable", true);
		properties.put("mail.debug", false);
		properties.put("mail.smtp.connectiontimeout", 10000);

		mailSender.setJavaMailProperties(properties);

		return mailSender;
	}
}
