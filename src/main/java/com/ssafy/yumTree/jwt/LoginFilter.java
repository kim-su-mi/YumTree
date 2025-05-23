package com.ssafy.yumTree.jwt;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.yumTree.user.LoginDto;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.ssafy.yumTree.user.RefreshDao;
import com.ssafy.yumTree.user.RefreshDto;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
/**
 * 로그인 진행 
 */
public class LoginFilter extends UsernamePasswordAuthenticationFilter{
	private final AuthenticationManager authenticationManager;
	//JWTUtil 주입
	private final JWTUtil jwtUtil;
	
	private final RefreshDao refreshDao;
	
	public LoginFilter(AuthenticationManager authenticationManager,JWTUtil jwtUtil,RefreshDao refreshDao) {
		this.authenticationManager = authenticationManager;
		this.jwtUtil = jwtUtil;
		this.refreshDao = refreshDao;
	}


	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException{

		// 클라이언트 요청에서 username, password추출
		String useId = obtainUsername(request); // 인증에 사용되는 주요 식별자를 가져오는 메서드
		String password = obtainPassword(request);

//		System.out.println(useId);

		// 스프링 시큐리티에서 username과 password를 검증하기 위해서 token애 담음
		UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(useId, password,null); // role값은 일단 null로

		//token에 담은 검증을위한 AuthenticationManager로 전
		return authenticationManager.authenticate(authToken);

	}
	
	//로그인 성공 시 실행 => JWT토큰 발급
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) {
		//유저 정보가져옴 
	    String userId = authentication.getName();

	    Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
	    Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
	    GrantedAuthority auth = iterator.next();
	    String role = auth.getAuthority();

	    //토큰 생성 (2개)
	    String access = jwtUtil.createJwt("access", userId, role, 600000L);
	    String refresh = jwtUtil.createJwt("refresh", userId, role, 86400000L);

	    System.out.println("생성한 refresh : "+refresh);
	  //Refresh 토큰 저장
	    addRefreshEntity(userId, refresh, 86400000L);
	    
	    //응답 설정
	    response.setHeader("access", access);
	    response.addCookie(createCookie("refresh", refresh));
	    response.setStatus(HttpStatus.OK.value());
		

	}
	
	private Cookie createCookie(String key, String value) {

	    Cookie cookie = new Cookie(key, value);
	    cookie.setMaxAge(24*60*60);
	    //cookie.setSecure(true);
	    //cookie.setPath("/");
	    cookie.setHttpOnly(true);

	    return cookie;
	}
	
	private void addRefreshEntity(String userId, String refresh, Long expiredMs) {

	    Date date = new Date(System.currentTimeMillis() + expiredMs);

	    RefreshDto refreshDto = new RefreshDto();
	    refreshDto.setUserId(userId);
	    refreshDto.setRefresh(refresh);
	    refreshDto.setExpiration(date.toString());

	    refreshDao.insertRefreshToken(refreshDto);
	}


	//로그인 실패 시 실행 
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,AuthenticationException failed) {
		System.out.println("로그인 실패  ");
		//로그인 실패시 401 응답 코드 반환
        response.setStatus(401);
	}

}
