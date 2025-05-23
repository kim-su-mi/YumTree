package com.ssafy.yumTree.diet;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;

import com.ssafy.yumTree.jwt.UserUtil;

import io.jsonwebtoken.io.IOException;

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
	 *프론트에서 file이라고 넘겨줘야
	 * @param request
	 * @return
	 * @throws Exception 
	 * @throws java.io.IOException 
	 * @throws Exception
	 */
	@PostMapping("/image")
	public Map<String, Object> imageUpload(MultipartFile file) throws Exception{

        Map<String, Object> responseData = new HashMap<>();

        try {

            String s3Url = dietService.imageUpload(file);
//            System.out.println("fullUrl : "+s3Url);
            responseData.put("uploaded", true);
            responseData.put("url", s3Url);

            return responseData;

        } catch (IOException e) {

            responseData.put("uploaded", false);

            return responseData;
        }
    }
	
	/**
     * 이미지 업로드 + AI 분석 통합 API
     * 프론트에서는 사진만 보내면 됨
     * @param file
     * @return
     * @throws Exception
     */
	@PostMapping("/analyze")
//	public ResponseEntity<Map<String, Object>> analyzeFood(MultipartFile file) throws Exception {
//	    Map<String, Object> responseData = new HashMap<>();
//	    System.out.println("컨트롤러 진입!");
//	    
//	    try {
//	        if (file == null || file.isEmpty()) {
//	            responseData.put("success", false);
//	            responseData.put("error", "파일이 없습니다.");
//	            return ResponseEntity.badRequest().body(responseData);
//	        }
//
//	        // 통합 분석 서비스 호출 (이미지 업로드 + AI 분석)
//	        Map<String, Object> result = dietService.analyzeFood(file);
//
//	        responseData.put("success", true);
//	        responseData.putAll(result);
//
//	        return ResponseEntity.ok(responseData);
//
//	    } catch (Exception e) {
//	        e.printStackTrace();
//	        responseData.put("success", false);
//	        responseData.put("error", e.getMessage());
//	        return ResponseEntity.status(500).body(responseData);
//	    }
//	}
	public ResponseEntity<Map<String, Object>> analyzeFood(MultipartFile file) throws Exception {
	    Map<String, Object> responseData = new HashMap<>();
	    
	    try {
	        if (file == null || file.isEmpty()) {
	            responseData.put("success", false);
	            responseData.put("error", "파일이 없습니다.");
	            return ResponseEntity.badRequest().body(responseData);
	        }
	        
	        // 통합 분석 서비스 호출
	        Map<String, Object> result = dietService.analyzeFood(file);
	        System.out.println("#########################최종 결과#################");
	        System.out.println(result);
	        return ResponseEntity.ok(result);
	        
	    } catch (Exception e) {
	        e.printStackTrace();
	        responseData.put("success", false);
	        responseData.put("error", e.getMessage());
	        return ResponseEntity.status(500).body(responseData);
	    }
	}
}
