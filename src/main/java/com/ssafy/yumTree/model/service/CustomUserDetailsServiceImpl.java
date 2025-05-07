package com.ssafy.yumTree.model.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.ssafy.yumTree.model.dao.SignupDao;
import com.ssafy.yumTree.model.dto.CustomUserDetails;
import com.ssafy.yumTree.model.dto.UserDto;

@Service
public class CustomUserDetailsServiceImpl implements UserDetailsService {
	private final SignupDao signupDao;
	
	public CustomUserDetailsServiceImpl(SignupDao signupDao) {
		this.signupDao = signupDao;
	}
	

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// 디비에서 해당 이름을 가진 사용자 조
		UserDto user = signupDao.findByUserName(username);
		
		//UserDetails에 담아서 return 하면 AuthenticationManager가 검증 
		if(user != null) {
			// 
			return new CustomUserDetails(user);
		}
		return null;
	}

}
