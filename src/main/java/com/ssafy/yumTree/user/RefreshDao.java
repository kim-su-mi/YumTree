package com.ssafy.yumTree.user;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.transaction.annotation.Transactional;

@Mapper
public interface RefreshDao{
	// 해당 refresh토큰이 db에 존재하는지 
	Boolean existsByRefresh(String refresh);

	//refresh토큰 삭제 
    @Transactional
    void deleteByRefresh(String refresh);

    //refresh토큰 디비에 저장 
	void insertRefreshToken(RefreshDto refreshDto);

}