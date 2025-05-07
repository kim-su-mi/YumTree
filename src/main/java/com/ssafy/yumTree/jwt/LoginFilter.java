package com.ssafy.yumTree.jwt;



import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class LoginFilter extends UsernamePasswordAuthenticationFilter{
	private final AuthenticationManager authenticationManager;
	
	public LoginFilter(AuthenticationManager authenticationManager) {
		this.authenticationManager = authenticationManager;
	}
	

	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException{
		
		// 클라이언트 요청에서 username, password추출 
		String userName = obtainUsername(request);
		String password = obtainPassword(request);
		
	System.out.println(userName);
		
		// 스프링 시큐리티에서 username과 password를 검증하기 위해서 token애 담음
		UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userName, password,null); // role값은 일단 null로 
		
		//token에 담은 검증을위한 AuthenticationManager로 전
		return authenticationManager.authenticate(authToken);
		
	}
	
	//로그인 성공 시 실행 => JWT토큰 발
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) {
		
	}
	
	//로그인 실패 시 실행 
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,AuthenticationException failed) {
		
	}

}
