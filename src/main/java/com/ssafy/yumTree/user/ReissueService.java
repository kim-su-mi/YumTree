package com.ssafy.yumTree.user;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface ReissueService {
	ResponseEntity<?> reissueToken(HttpServletRequest request, HttpServletResponse response);

}
