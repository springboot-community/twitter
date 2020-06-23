package io.springboot.twitter.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import io.springboot.twitter.web.interceptor.UserTokenInterceptor;


@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(this.userTokenInterceptor())
			.addPathPatterns("/**")
			.excludePathPatterns("/oauth2/**", "/test/**", "/error/**");
	}
	
	@Bean
	public UserTokenInterceptor userTokenInterceptor () {
		return new UserTokenInterceptor();
	}
}
