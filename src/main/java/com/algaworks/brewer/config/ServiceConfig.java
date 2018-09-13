package com.algaworks.brewer.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan({ "com.algaworks.brewer.service", "com.algaworks.brewer.storage" })
public class ServiceConfig {

}
