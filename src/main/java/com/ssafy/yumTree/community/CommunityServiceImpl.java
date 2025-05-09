package com.ssafy.yumTree.community;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class CommunityServiceImpl implements CommunityService {

    private CommunityDao communityDao;

    public CommunityServiceImpl(CommunityDao communityDao) {
        this.communityDao = communityDao;
    }

    @Override
    public void writeCommunity(CommunityDto communityDto) {
        communityDao.insert(communityDto);
    }

    @Override
    public List<CommunityDto> getAllCommunity() {
        return communityDao.getAllCommunity();
    }

    @Override
    public CommunityDto getCommunityById(int cm_number) {
        return communityDao.getCommunity(cm_number);
    }

    @Override
    public void updateCommunity(CommunityDto communityDto) {
        communityDao.update(communityDto);
    }

    @Override
    public void deleteCommunity(int cm_number) {
        communityDao.delete(cm_number);
    }
}
