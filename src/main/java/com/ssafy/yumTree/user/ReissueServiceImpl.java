package com.ssafy.yumTree.user;

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

    public ReissueServiceImpl(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
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

        String userId = jwtUtil.getUseId(refresh);
        String role = jwtUtil.getRole(refresh);

        //새로운 access토큰 만듦 
        String newAccess = jwtUtil.createJwt("access", userId, role, 600000L);

        //response헤더에 토큰 넣어서 반환 
        response.setHeader("access", newAccess);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
