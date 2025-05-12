package com.ssafy.yumTree.community;


import java.util.List;

import org.apache.ibatis.annotations.Mapper;
@Mapper
public interface CommunityDao {
    void insert(CommunityDto communityDto);
    CommunityDto getCommunity(int cmNumber);
    List<CommunityDto> getAllCommunity();
    void update(CommunityDto communityDto);
    void delete(int cmNumber);
}
