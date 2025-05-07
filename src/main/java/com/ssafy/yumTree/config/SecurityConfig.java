package com.ssafy.yumTree.config;


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

import com.ssafy.yumTree.jwt.LoginFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
	// AuthenticationManager가 인자로 받을 AuthenticationConfiguration객체 생성자 주입 
	private final AuthenticationConfiguration authenticationConfiguration;
	
	public SecurityConfig(AuthenticationConfiguration authenticationConfiguration) {
		this.authenticationConfiguration = authenticationConfiguration;
	}
	
	// 비밀번호 암호화 설정 
	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	//AuthenticationManager를 빈으로 등
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
		return configuration.getAuthenticationManager();
	}

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

				//csrf disable
        http
                .csrf((auth) -> auth.disable());

				//From 로그인 방식 disable
        http
                .formLogin((auth) -> auth.disable());

				//http basic 인증 방식 disable
        http
                .httpBasic((auth) -> auth.disable());

				//경로별 인가 작업
        http
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/login", "/", "/signup").permitAll() 
						.requestMatchers("/admin").hasRole("ADMIN")
                        .anyRequest().authenticated()
//        		.anyRequest().permitAll() // 모든 요청 인증 없이 허용
                        );
        // 필터 추가 LoginFilter()는 인자를 받음 (authenticationManager메소드에 authenticationConfiguration객체를 인자로 넣어야함) 
        http
        		.addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration)), UsernamePasswordAuthenticationFilter.class);

				//세션 설정
        http
                .sessionManagement((session) -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
    

}