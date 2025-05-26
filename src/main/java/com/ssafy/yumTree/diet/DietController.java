package com.ssafy.yumTree.diet;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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
	 * 식단 저장 
	 * @param requestDto
	 * @return
	 */
	@PostMapping("")
    public ResponseEntity<DietSaveResponseDto> saveDiet(@RequestBody DietSaveRequestDto requestDto) {
        try {
            DietSaveResponseDto response = dietService.saveDiet(requestDto);
            
            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new DietSaveResponseDto(false, "서버 오류가 발생했습니다."));
        }
    }

	/**
	 * 식단 페이지 로드 시 달력에 뿌려줄 현재 달에 해당하는 유저의 모든 식단 기록 조회 
	 * 프론트에서 쿼리 파람으로 '2025-05-20'식으로 날짜 내줘야함 
	 * @param date
	 * @return
	 */
	@GetMapping("{date}")
	public ResponseEntity<MonthlyDietSummaryResponseDto> getMontlyDietLog(@PathVariable String date) {
        MonthlyDietSummaryResponseDto summary = dietService.getMonthlySummary(date);
        return ResponseEntity.ok(summary);
    }
	
	/**
     * 특정 날짜의 식단 상세 정보 조회
     * @param date 날짜 (YYYY-MM-DD 형식)
     * @return 해당 날짜의 식단 상세 정보
     */
    @GetMapping("/daily/{date}")
    public ResponseEntity<DailyDietResponseDto> getDailyDiet(@PathVariable String date) {
        try {
            // 날짜 형식 검증
            LocalDate.parse(date); // 잘못된 형식이면 예외 발생
            
            DailyDietResponseDto response = dietService.getDailyDiet(date);
            return ResponseEntity.ok(response);
            
        } catch (DateTimeParseException e) {
            // 잘못된 날짜 형식
            return ResponseEntity.badRequest()
                .body(new DailyDietResponseDto(false, null));
        } catch (Exception e) {
            // 기타 서버 오류
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new DailyDietResponseDto(false, null));
        }
    }
    
    /**
     * 특정 날짜의 특정 음식 상세 정보 조회
     * @param date 날짜 (YYYY-MM-DD 형식)
     * @param foodId 음식 ID
     * @return 해당 음식의 상세 영양성분 정보
     */
    @GetMapping("/daily/{date}/{foodId}")
    public ResponseEntity<FoodDetailResponseDto> getFoodDetailByDate(
            @PathVariable String date, 
            @PathVariable int foodId) {
        try {
            // 날짜 형식 검증
            LocalDate.parse(date);
            
            FoodDetailResponseDto response = dietService.getFoodDetailByDate(date, foodId);
            return ResponseEntity.ok(response);
            
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest()
                .body(new FoodDetailResponseDto(false, null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new FoodDetailResponseDto(false, null));
        }
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
	
	/**
	 * 식단 입력창에서 음식 조회
	 */
	@GetMapping("/food")
	public ResponseEntity<List<FoodDto>> getSearchFood(String search){
		List<FoodDto> foods = dietService.getSearchFood(search);
		System.out.println(foods);
		return ResponseEntity.ok().body(foods);
	}
	
}
