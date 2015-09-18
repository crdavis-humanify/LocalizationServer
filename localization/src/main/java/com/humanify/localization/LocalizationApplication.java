package com.humanify.localization;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.DispatcherServlet;

import com.humanify.localization.service.LocalizationService;

// TODO
// admin security
// update notifications


@SpringBootApplication
public class LocalizationApplication
{
	
	@Autowired
	LocalizationService service;
	
	public static void main(String[] args)
	{
		SpringApplication.run(LocalizationApplication.class, args);
	}
	
}

@Configuration
class LocalizationConfig
{
	@Bean
	public DispatcherServlet dispatcherServlet()
	{
		DispatcherServlet ds = new DispatcherServlet();
		ds.setThrowExceptionIfNoHandlerFound(true);
		return ds;
	}
}