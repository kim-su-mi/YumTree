package com.ssafy.yumTree.user;

import java.util.Date;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.ssafy.yumTree.jwt.JWTUtil;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class ReissueServiceImpl implements ReissueService {

	private final JWTUtil jwtUtil;
	private final RefreshDao refreshDao;

    public ReissueServiceImpl(JWTUtil jwtUtil,RefreshDao refreshDao) {
        this.jwtUtil = jwtUtil;
    	this.refreshDao = refreshDao;
    }

    @Override
    public ResponseEntity<?> reissueToken(HttpServletRequest request, HttpServletResponse response) {
        // refresh token을 쿠키에서 뽑음 
        String refresh = null;
        Cookie[] cookies = request.getCookies();
        // 쿠키 순회하면서 refresh토큰이 있으면 저장 
        for (Cookie cookie : cookies) {

            if (cookie.getName().equals("refresh")) {

                refresh = cookie.getValue();
            }
        }

        if (refresh == null) {
            //response status code
            return new ResponseEntity<>("refresh token null", HttpStatus.BAD_REQUEST);
        }

        // 여기까지 왔으면 리프레시 토큰이 있음 
        //expired check
        try {
            jwtUtil.isExpired(refresh);
        } catch (ExpiredJwtException e) {

            //response status code
            return new ResponseEntity<>("refresh token expired", HttpStatus.BAD_REQUEST);
        }

        // 토큰이 refresh인지 확인 (발급시 페이로드에 명시)
        String category = jwtUtil.getCategory(refresh);

        if (!category.equals("refresh")) {

            //response status code
            return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
        }
        
      //DB에 refresh 토큰이 저장되어 있는지 확인
    	Boolean isExist = refreshDao.existsByRefresh(refresh);
    	System.out.println("isExist 여부 : "+isExist);
    	if (!isExist) {
    		
    		   //response body
    		   return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
    	}

        String userId = jwtUtil.getUseId(refresh);
        String role = jwtUtil.getRole(refresh);

        //새로운 access토큰,refresh토큰  만듦 
        String newAccess = jwtUtil.createJwt("access", userId, role, 600000L);
        String newRefresh = jwtUtil.createJwt("refresh", userId, role, 86400000L);
        
      //Refresh 토큰 저장 DB에 기존의 Refresh 토큰 삭제 후 새 Refresh 토큰 저장
    	refreshDao.deleteByRefresh(refresh);
    	addRefreshEntity(userId, newRefresh, 86400000L);
        
        //response헤더에 토큰 넣어서 반환 
        response.setHeader("access", newAccess);
        response.addCookie(createCookie("refresh", newRefresh));
        
        return new ResponseEntity<>(HttpStatus.OK);
    }
    
    /**
     * 리프레시 토큰을 디비에 저장 
     * @param userId
     * @param refresh
     * @param expiredMs
     */
    private void addRefreshEntity(String userId, String refresh, Long expiredMs) {

        Date date = new Date(System.currentTimeMillis() + expiredMs);

        RefreshDto refreshDto = new RefreshDto();
        refreshDto.setUserId(userId);
        refreshDto.setRefresh(refresh);
        refreshDto.setExpiration(date.toString());

        refreshDao.insertRefreshToken(refreshDto);
    }
    
    /**
     * 쿠키 생성 
     * @param key
     * @param value
     * @return
     */
    private Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24*60*60);
        //cookie.setSecure(true);
        //cookie.setPath("/");
        cookie.setHttpOnly(true);

        return cookie;
    }
    

}
