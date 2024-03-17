package com.ar.pckart.admin.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AdminFilterConfig {
	
	@Autowired JwtAdminService jwtAdminService;
	
	@Value("${filter.url.patterns}")
	private String[] URL_PATTERNS;
	
	@Bean
	public FilterRegistrationBean<AdminFilter> customFilterRegistrationBean(){
		FilterRegistrationBean<AdminFilter> registrationBean =
				new FilterRegistrationBean<>();
		registrationBean.setFilter(new AdminFilter(jwtAdminService));
		registrationBean.addUrlPatterns(URL_PATTERNS);  // "/*"
		return registrationBean;
	}
	
}


/*
	private final String[] URL_PATTERNS = {
			"/pckart/api/v1/brands/auth/*",
			"/pckart/api/v1/categories/auth/*",
			"/pckart/api/v1/products/auth/*",
	};
*/