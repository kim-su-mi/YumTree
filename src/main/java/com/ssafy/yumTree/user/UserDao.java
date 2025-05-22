package com.ssafy.yumTree.user;

import org.apache.ibatis.annotations.Mapper;
@Mapper
public interface UserDao {
	void insertUser(UserDto dto);
	
	int existsByUserId(String userId);
	
	//username을 받아 DB테이블에서 회원을 조회하는 메소
	UserDto findByUserName(String userId);
	
	// userId로 user_number조회 UserUtil에서 사용
	int findUserNumberByUserId(String userId);

}
