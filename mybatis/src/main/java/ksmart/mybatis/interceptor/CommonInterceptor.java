package ksmart.mybatis.interceptor;

import java.util.Set;
import java.util.StringJoiner;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CommonInterceptor implements HandlerInterceptor{
	/**
	 * 매개변수: HttpServletRequest, HttpServletResponse, Object handler  
  	 * HandlerMapping이 핸들러(controller) 객체를 결정한 후 HandlerAdapter가 핸들러를 호출하기 전에 호출되는 메소드
  	 * return true(cotroller 진입), false(cotroller 진입 차단)
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		
		Set<String> paramkeySet = request.getParameterMap().keySet();
		
		StringJoiner param = new StringJoiner(", ");
		
		for(String key : paramkeySet) {
			//memberId=id001 -> memberId: id001
			//memberId=id001&memberPw=pw001 -> memberId: id001, memberPw: pw001
			param.add(key + ": " + request.getParameter(key));
		}
		
		log.info("ACCESS INFO======================================================");
		log.info("PORT		::::::		{}", request.getLocalPort());
		log.info("ServerName		::::::		{}", request.getServerName());
		log.info("HTTPMethod		::::::		{}", request.getMethod());
		log.info("URI			::::::		{}", request.getRequestURI());
		log.info("CLIENT IP		::::::		{}", request.getRemoteAddr());
		log.info("Parameter		::::::		{}", param);
		log.info("ACCESS INFO======================================================");
		
		return HandlerInterceptor.super.preHandle(request, response, handler);
	}
	
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		// TODO Auto-generated method stub
		HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
	}
	
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		// TODO Auto-generated method stub
		HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
	}

}
