package com.ssafy.yumTree.community;


import java.util.List;

public interface CommunityService {
    void writeCommunity(CommunityDto communityDto);
    List<CommunityDto> getAllCommunity();
    CommunityDto getCommunityById(int cm_number);
    void updateCommunity(CommunityDto communityDto);
    void deleteCommunity(int cm_number);
}
