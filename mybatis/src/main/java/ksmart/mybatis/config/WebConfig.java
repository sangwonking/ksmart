package ksmart.mybatis.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import ksmart.mybatis.interceptor.CommonInterceptor;
import ksmart.mybatis.interceptor.LoginInterceptor;
import lombok.AllArgsConstructor;

@Configuration
@AllArgsConstructor
public class WebConfig implements WebMvcConfigurer{

	private final CommonInterceptor commonInterceptor;
	private final LoginInterceptor loginInterceptor;
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		
		registry.addInterceptor(commonInterceptor)
				.addPathPatterns("/**")
				.excludePathPatterns("/css/**")
				.excludePathPatterns("/js/**")
				.excludePathPatterns("/favicon.ico");
		
		registry.addInterceptor(loginInterceptor)
				.addPathPatterns("/**")
				.excludePathPatterns("/css/**")
				.excludePathPatterns("/js/**")
				.excludePathPatterns("/favicon.ico")
				.excludePathPatterns("/")
				.excludePathPatterns("/member/login")
				.excludePathPatterns("/member/logout")
				.excludePathPatterns("/member/addMember")
				.excludePathPatterns("/member/idCheck");
				
		
		WebMvcConfigurer.super.addInterceptors(registry);
	}
}

















