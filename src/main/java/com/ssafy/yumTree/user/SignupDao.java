package com.ssafy.yumTree.user;


public interface SignupDao {
	void insertUser(UserDto dto);
	
	int existsByUserId(String userId);
	
	//username을 받아 DB테이블에서 회원을 조회하는 메소
	UserDto findByUserName(String username);

}
