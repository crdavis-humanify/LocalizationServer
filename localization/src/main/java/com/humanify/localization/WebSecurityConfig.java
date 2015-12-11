package com.humanify.localization;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter
{
	@Override
	protected void configure(HttpSecurity http) throws Exception
	{
		// TODO: need to implement tenant-level security for /localized/**/tenant/** and /tenant/**
		// TODO: should we require user role for any of the non-tenant-specific GETs?
		
		http
			.csrf()
				.disable()
			.authorizeRequests()
				.antMatchers("/debug/**").hasRole("ADMIN")					// Must be an admin to use /debug endpoints
				.antMatchers(HttpMethod.POST, "/**").hasRole("ADMIN")		// Must be an admin to install/update data
				.anyRequest().permitAll()
				.and()
			.httpBasic()
//				.and()
//			.formLogin()
//				.permitAll()
			;
	}
	
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception
	{
		auth.inMemoryAuthentication()
			.withUser("admin").password("admin").roles("ADMIN", "USER").and()
			.withUser("user").password("user").roles("USER");
	}
}
