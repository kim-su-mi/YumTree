package com.ssafy.yumTree.config;

import java.util.Arrays;
import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import com.ssafy.yumTree.jwt.CustomLogoutFilter;
import com.ssafy.yumTree.jwt.JWTFilter;
import com.ssafy.yumTree.jwt.JWTUtil;
import com.ssafy.yumTree.jwt.LoginFilter;
import com.ssafy.yumTree.user.RefreshDao;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	// AuthenticationManager가 인자로 받을 AuthenticationConfiguration객체 생성자 주입
	private final AuthenticationConfiguration authenticationConfiguration;
	// JWTUtil 주입
	private final JWTUtil jwtUtil;
	
	private final RefreshDao refreshDao;

	public SecurityConfig(AuthenticationConfiguration authenticationConfiguration, JWTUtil jwtUtil,RefreshDao refreshDao) {
		this.authenticationConfiguration = authenticationConfiguration;
		this.jwtUtil = jwtUtil;
		this.refreshDao = refreshDao;
	}

	// 비밀번호 암호화 설정
	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

	// AuthenticationManager를 빈으로 등
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
		return configuration.getAuthenticationManager();
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

		// cors설정
		http.cors((corsCustomizer -> corsCustomizer.configurationSource(new CorsConfigurationSource() {

			@Override
			public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {

				CorsConfiguration configuration = new CorsConfiguration();

				configuration.setAllowedOrigins(
						Arrays.asList("http://localhost:3000", "http://localhost:5173")
				); //프론트 엔드 서버 주
				configuration.setAllowedMethods(Collections.singletonList("*")); //허용 메서드
				configuration.setAllowCredentials(true);
				configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
				configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "access"));
				configuration.setExposedHeaders(Arrays.asList("Authorization", "access")); // 반환할 헤더
				configuration.setAllowCredentials(true);
				configuration.setMaxAge(3600L);
				return configuration;
			}
		})));

		// csrf disable
		http.csrf((auth) -> auth.disable());

		// From 로그인 방식 disable
		http.formLogin((auth) -> auth.disable());

		// http basic 인증 방식 disable
		http.httpBasic((auth) -> auth.disable());

		// 경로별 인가 작업
		http.authorizeHttpRequests((auth) -> auth
				.requestMatchers("/login", "/", "/signup","/community").permitAll()
				.requestMatchers("/admin").hasRole("ADMIN")
				.requestMatchers("/reissue").permitAll()
				.anyRequest().authenticated()
//        		.anyRequest().permitAll() // 모든 요청 인증 없이 허용
		);
		// JWTFilter 등록
		http.addFilterBefore(new JWTFilter(jwtUtil), LoginFilter.class);

		// 필터 추가 LoginFilter()는 인자를 받음 (authenticationManager메소드에
		// authenticationConfiguration객체를 인자로 넣어야함)
		http.addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil,refreshDao),
				UsernamePasswordAuthenticationFilter.class);

		// 커스텀한 로그아웃 필터 등록 
		http
        	.addFilterBefore(new CustomLogoutFilter(jwtUtil, refreshDao), LogoutFilter.class);
		
		// 세션 설정
		http.sessionManagement((session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

		return http.build();
	}

}