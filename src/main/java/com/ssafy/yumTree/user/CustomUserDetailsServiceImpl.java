package com.ssafy.yumTree.user;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsServiceImpl implements UserDetailsService {
	private final UserDao signupDao;
	
	public CustomUserDetailsServiceImpl(UserDao signupDao) {
		this.signupDao = signupDao;
	}
	

	@Override
	public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
		// 디비에서 해당 이름을 가진 사용자 조
		UserDto user = signupDao.findByUserName(userId);
		
		//UserDetails에 담아서 return 하면 AuthenticationManager가 검증 
		if(user != null) {
			// 
			return new CustomUserDetails(user);
		}
		return null;
	}

}
