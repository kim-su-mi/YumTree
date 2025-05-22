package com.ssafy.yumTree.diet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartRequest;

import com.ssafy.yumTree.jwt.UserUtil;

@RestController
@RequestMapping("/diet")
public class DietController {
	private final DietService dietService;
	
	private UserUtil userUtil;
	
	public DietController(DietService dietService,UserUtil userUtil) {
		this.dietService = dietService;
		this.userUtil = userUtil;
	}
	
	/**
	 * 음식 디비에 음식 직접 등록 
	 * @param foodDto
	 * @return
	 */
	@PostMapping("/food")
	public ResponseEntity<Void> addFood(@RequestBody FoodDto foodDto){
//		System.out.println("넘버 : "+userUtil.getCurrentUserNumber());
		dietService.addFood(foodDto);
		return ResponseEntity.ok().build();
	}
	

	/**
	 * 식단 페이지 로드 시 달력에 뿌려줄 현재 달에 해당하는 유저의 모든 식단 기록 조회 
	 * 프론트에서 쿼리 파람으로 '2025-05-20'식으로 날짜 내줘야함 
	 * @param date
	 * @return
	 */
	@GetMapping("")
	public ResponseEntity<MonthlyDietSummaryResponseDto> getMontlyDietLog(@RequestParam String date){
//		return ResponseEntity.ok().body(dietService.getMontlyDietLog(date));
		MonthlyDietSummaryResponseDto summary = dietService.getMonthlySummary(date);
        return ResponseEntity.ok(summary);
	}
	
	/**
	 *s3에 이미지 저장 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@PostMapping("/image")
	public Map<String, Object> imageUpload(MultipartRequest request) throws Exception {

//        Map<String, Object> responseData = new HashMap<>();
//
//        try {
//
//            String s3Url = dietService.imageUpload(request);
//
//            responseData.put("uploaded", true);
//            responseData.put("url", s3Url);
//
//            return responseData;
//
//        } catch (IOException e) {
//
//            responseData.put("uploaded", false);
//
//            return responseData;
//        }
		return null;
    }

}
