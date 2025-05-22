package com.ssafy.yumTree.jwt;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.ssafy.yumTree.user.CustomUserDetails;
import com.ssafy.yumTree.user.UserDao;
@Component
public class UserUtil {
	private final UserDao userDao;
	
	public UserUtil(UserDao userDao) {
		this.userDao = userDao;
	}

	/**
     * 현재 인증된 사용자의 userId 가져오기
     */
    public String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.isAuthenticated() && 
            !"anonymousUser".equals(authentication.getPrincipal())) {
            
            // CustomUserDetails에서 userId 추출
            if (authentication.getPrincipal() instanceof CustomUserDetails) {
                CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
                return userDetails.getUsername(); // userId가 저장된 필드
            }
        }
        
        return null; // 인증되지 않은 경우
    }
    
    /**
     * 현재 인증된 사용자의 user_number(DB상의 사용자 번호) 가져오기
     */
    public Integer getCurrentUserNumber() {
        String userId = getCurrentUserId();
        
        if (userId != null) {
            
            return userDao.findUserNumberByUserId(userId);
        }
        
        return null;
    }
    
    
}
