package com.ssafy.yumTree.jwt;

import java.io.IOException;

import org.springframework.web.filter.GenericFilterBean;

import com.ssafy.yumTree.user.RefreshDao;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
/**
 * 로그아웃 필터 
 */
public class CustomLogoutFilter extends GenericFilterBean {

    private final JWTUtil jwtUtil;
    private final RefreshDao refreshDao;

    public CustomLogoutFilter(JWTUtil jwtUtil, RefreshDao refreshRepository) {

        this.jwtUtil = jwtUtil;
        this.refreshDao = refreshRepository;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
    }

    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {

        //url이 로그아웃 경로인지 아닌지 검증 
        String requestUri = request.getRequestURI();
        
     // context-path를 제거한 실제 경로 체크
        String contextPath = request.getContextPath(); // "/yumTree"
        String path = requestUri.substring(contextPath.length()); // "/logout"
        
        if (!path.matches("^\\/logout$")) {

            filterChain.doFilter(request, response);
            return;
        }
        String requestMethod = request.getMethod();
        if (!requestMethod.equals("POST")) { // 로그아웃 경로인데 POST요청이 아니면 다음 필터로 넘어감 

            filterChain.doFilter(request, response);
            return;
        }

        //쿠키들에서 refresh token 추출 
        String refresh = null;
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {

            if (cookie.getName().equals("refresh")) {

                refresh = cookie.getValue();
            }
        }

        //refresh 토큰이 null인지 체크 
        if (refresh == null) {
        	System.out.println("토큰 null");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        System.out.println("토큰 null x ");
        //refresh 토큰 만료 여부 체크 
        try {
        	System.out.println("토큰 만료 ");
            jwtUtil.isExpired(refresh);
        } catch (ExpiredJwtException e) {
        	// 만료 되었다는 건 이미 로그아웃 되었다는 의미
            //response status code
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        System.out.println("토큰 만x");
        // 토큰이 refresh인지 확인 (발급시 페이로드에 명시)
        String category = jwtUtil.getCategory(refresh);
        if (!category.equals("refresh")) {

            //response status code
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        //DB에 저장되어 있는지 확인
        System.out.println(refresh);
        Boolean isExist = refreshDao.existsByRefresh(refresh);
        if (!isExist) {
        	System.out.println("토큰 디비에 없음 ");
            //response status code
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        System.out.println("토큰 디비에 있음  ");
        
        //로그아웃 진행
        //Refresh 토큰 DB에서 제거
        refreshDao.deleteByRefresh(refresh);
        System.out.println("토큰 디비에서제거 완료  ");

        //Refresh 토큰 Cookie 값 0
        Cookie cookie = new Cookie("refresh", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");

        System.out.println("쿠키 셋팅 ");
        
        response.addCookie(cookie);
        response.setStatus(HttpServletResponse.SC_OK);
    }

	
}
