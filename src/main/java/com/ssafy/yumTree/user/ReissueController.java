package com.ssafy.yumTree.user;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.yumTree.jwt.JWTUtil;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
public class ReissueController {
	private final ReissueService reissueService;

	public ReissueController(ReissueService reissueService) {
		this.reissueService = reissueService;
	}

	/**
	 *refresh 토큰을 받아 새로운 Access토큰을 발급해줌 
	 * @param request
	 * @param response
	 * @return
	 */
	@PostMapping("/reissue")
    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {
        return reissueService.reissueToken(request, response);
    }
//	private final JWTUtil jwtUtil;
//
//    public ReissueController(JWTUtil jwtUtil) {
//
//        this.jwtUtil = jwtUtil;
//    }
//
//    @PostMapping("/reissue")
//    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {
//
//        //get refresh token
//        String refresh = null;
//        Cookie[] cookies = request.getCookies();
//        for (Cookie cookie : cookies) {
//
//            if (cookie.getName().equals("refresh")) {
//
//                refresh = cookie.getValue();
//            }
//        }
//
//        if (refresh == null) {
//
//            //response status code
//            return new ResponseEntity<>("refresh token null", HttpStatus.BAD_REQUEST);
//        }
//
//        //expired check
//        try {
//            jwtUtil.isExpired(refresh);
//        } catch (ExpiredJwtException e) {
//
//            //response status code
//            return new ResponseEntity<>("refresh token expired", HttpStatus.BAD_REQUEST);
//        }
//
//        // 토큰이 refresh인지 확인 (발급시 페이로드에 명시)
//        String category = jwtUtil.getCategory(refresh);
//
//        if (!category.equals("refresh")) {
//
//            //response status code
//            return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
//        }
//
//        String username = jwtUtil.getUseId(refresh);
//        String role = jwtUtil.getRole(refresh);
//
//        //make new JWT
//        String newAccess = jwtUtil.createJwt("access", username, role, 600000L);
//
//        //response
//        response.setHeader("access", newAccess);
//
//        return new ResponseEntity<>(HttpStatus.OK);
//    }
}