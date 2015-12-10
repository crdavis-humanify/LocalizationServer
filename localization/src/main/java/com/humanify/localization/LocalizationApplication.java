package com.humanify.localization;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.DispatcherServlet;

// TODO: publish update notifications on MQ bus


@SpringBootApplication
public class LocalizationApplication extends SpringBootServletInitializer
{
	
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder app)
	{
		return app.sources(LocalizationApplication.class);
	}
	
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