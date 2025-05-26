package com.ssafy.yumTree.diet;

import java.io.IOException;
import java.math.BigInteger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssafy.yumTree.config.ChatGPTConfig;
import com.ssafy.yumTree.config.S3Config;
import com.ssafy.yumTree.jwt.UserUtil;



@Service
public class DietServiceImpl implements DietService{
	private final DietDao dietDao;
	private final UserUtil userUtil;
	private final S3Config s3Config;
	private final ChatGPTConfig chatGPTConfig;
	
	public DietServiceImpl(DietDao dietDao,UserUtil userUtil,S3Config s3Config,ChatGPTConfig chatGPTConfig) {
		this.dietDao = dietDao;
		this.userUtil = userUtil;
		this.s3Config = s3Config;
		this.chatGPTConfig = chatGPTConfig;
	}

	@Override
	public int addFood(FoodDto foodDto) {
		
		return dietDao.insertFood(foodDto);
	}
	
	@Value("${cloud.aws.s3.bucket}")
    private String bucket;
	@Value("${cloud.aws.s3.url.prefix}")
	private String prefix;
	
	private String promptUrl = "https://api.openai.com/v1/chat/completions";

	// 고정 프롬프트 설정
	private final String FIXED_PROMPT = "이 음식 사진을 분석해서 다음 정보를 정확한 JSON 형태로 제공해주세요:\n" +
	        "1. 각 음식의 이름 (한국어)\n" +
	        "2. 각 음식의 예상 용량(g)\n\n" +
	        "응답은 반드시 다음 JSON 형식만 사용해주세요:\n" +
	        "{\n" +
	        "  \"foods\": [\n" +
	        "    {\n" +
	        "      \"name\": \"음식이름\",\n" +
	        "      \"weight\": 숫자\n" +
	        "    }\n" +
	        "  ]\n" +
	        "}\n\n" +
	        "다른 설명 없이 오직 JSON만 응답해주세요.";

	
	/**
     * 음식 검색
     */
	@Override
	public List<FoodDto> getSearchFood(String search) {
		return dietDao.selectFoodList(search);
	}
	
	/**
	 * 식단 저장 
	 */
	public DietSaveResponseDto saveDiet(DietSaveRequestDto requestDto) {
        try {
            // 입력값 검증
            if (!validateRequest(requestDto)) {
                return new DietSaveResponseDto(false, "잘못된 입력값입니다.");
            }
            
            // 현재 사용자 번호 가져오기
            int userNumber = userUtil.getCurrentUserNumber();
            
            // 같은 날짜, 같은 식사타입의 기존 데이터 확인
            Map<String, Object> checkParams = new HashMap<>();
            checkParams.put("userNumber", userNumber);
            checkParams.put("dietLogDate", requestDto.getDietLogDate());
            checkParams.put("mealType", requestDto.getMealType());
            
            Integer existingDietLogId = dietDao.findExistingDietLog(checkParams);
            
            if (existingDietLogId != null) {
                // 기존 데이터가 있는 경우 업데이트
                return updateExistingDiet(existingDietLogId, requestDto);
            } else {
                // 새로운 데이터 저장
                return saveNewDiet(userNumber, requestDto);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("식단 저장 중 오류가 발생했습니다.", e);
        }
    }
    
    private boolean validateRequest(DietSaveRequestDto requestDto) {
        if (requestDto.getDietLogDate() == null || requestDto.getDietLogDate().trim().isEmpty()) {
            return false;
        }
        if (requestDto.getMealType() == null || requestDto.getMealType().trim().isEmpty()) {
            return false;
        }
        if (requestDto.getDietDetails() == null || requestDto.getDietDetails().isEmpty()) {
            return false;
        }
        
        // 식사 타입 검증
        List<String> validMealTypes = Arrays.asList("아침", "점심", "저녁", "간식");
        if (!validMealTypes.contains(requestDto.getMealType())) {
            return false;
        }
        
        // 각 음식 상세 정보 검증
        for (DietDetailDto detail : requestDto.getDietDetails()) {
            if (detail.getFoodId() <= 0 || detail.getDietAmount() <= 0) {
                return false;
            }
            if (detail.getDietUnit() == null || detail.getDietUnit().trim().isEmpty()) {
                return false;
            }
            
            // 단위 검증
            List<String> validUnits = Arrays.asList("g", "인분", "조각", "반찬그릇");
            if (!validUnits.contains(detail.getDietUnit())) {
                return false;
            }
        }
        
        return true;
    }
    
    private DietSaveResponseDto saveNewDiet(int userNumber, DietSaveRequestDto requestDto) {
        // diet_log 테이블에 메인 정보 저장
        Map<String, Object> dietLogParams = new HashMap<>();
        dietLogParams.put("userNumber", userNumber);
        dietLogParams.put("dietLogDate", requestDto.getDietLogDate());
        dietLogParams.put("mealType", requestDto.getMealType());
        dietLogParams.put("dietImage", requestDto.getDietImage());
        
        int result = dietDao.insertDietLog(dietLogParams);
        
        // BigInteger를 Integer로 안전하게 변환
        Object dietLogIdObj = dietLogParams.get("dietLogId");
        int dietLogId = 0;
        if (dietLogIdObj instanceof BigInteger) {
            dietLogId = ((BigInteger) dietLogIdObj).intValue();
        } else if (dietLogIdObj instanceof Integer) {
            dietLogId = (Integer) dietLogIdObj;
        } else if (dietLogIdObj instanceof Long) {
            dietLogId = ((Long) dietLogIdObj).intValue();
        }
        
        if (result > 0 && dietLogId > 0) {
            // diet_log_detail 테이블에 상세 정보 저장
            for (DietDetailDto detail : requestDto.getDietDetails()) {
                Map<String, Object> detailParams = new HashMap<>();
                detailParams.put("dietLogId", dietLogId);
                detailParams.put("foodId", detail.getFoodId());
                detailParams.put("dietAmount", detail.getDietAmount());
                detailParams.put("dietUnit", detail.getDietUnit());
                
                dietDao.insertDietLogDetail(detailParams);
            }
            
            return new DietSaveResponseDto(true, "식단이 성공적으로 저장되었습니다.", dietLogId);
        } else {
            return new DietSaveResponseDto(false, "식단 저장에 실패했습니다.");
        }
    }
    
    private DietSaveResponseDto updateExistingDiet(int dietLogId, DietSaveRequestDto requestDto) {
        // 기존 상세 정보 삭제
        dietDao.deleteDietLogDetails(dietLogId);
        
        // 메인 정보 업데이트 (이미지만 업데이트)
        Map<String, Object> updateParams = new HashMap<>();
        updateParams.put("dietLogId", dietLogId);
        updateParams.put("dietImage", requestDto.getDietImage());
        dietDao.updateDietLogImage(updateParams);
        
        // 새로운 상세 정보 저장
        for (DietDetailDto detail : requestDto.getDietDetails()) {
            Map<String, Object> detailParams = new HashMap<>();
            detailParams.put("dietLogId", dietLogId);
            detailParams.put("foodId", detail.getFoodId());
            detailParams.put("dietAmount", detail.getDietAmount());
            detailParams.put("dietUnit", detail.getDietUnit());
            
            dietDao.insertDietLogDetail(detailParams);
        }
        
        return new DietSaveResponseDto(true, "식단이 성공적으로 업데이트되었습니다.", dietLogId);
    }


	
	/**
	 * 달력에 표시할 식단 정보 가져오기 
	 */
	@Override
	public MonthlyDietSummaryResponseDto getMonthlySummary(String dateStr) {
		
		// 현재 사용자 ID 가져오기
	     int userNumber = userUtil.getCurrentUserNumber();
		
	    LocalDate date = LocalDate.parse(dateStr);
	    LocalDate firstDayOfMonth = date.withDayOfMonth(1);
	    LocalDate lastDayOfMonth = date.withDayOfMonth(date.lengthOfMonth());
	    
	    Map<String, Object> map = new HashMap<>();
	     map.put("userNumber", userNumber);
	    map.put("firstDayOfMonth", firstDayOfMonth);
	    map.put("lastDayOfMonth", lastDayOfMonth);
	    
	    // DB에서 이미 boolean 형태로 가져옴
	    List<Map<String, Object>> monthlyData = dietDao.getDietSummaryByMonth(map);
	    
	    // 간단한 변환
	    Map<String, DailyDietSummaryDto> dietMap = monthlyData.stream()
	    	    .collect(Collectors.toMap(
	    	        data -> (String) data.get("date"),
	    	        data -> new DailyDietSummaryDto(
	    	            (String) data.get("date"),
	    	            ((Number) data.get("hasBreakfast")).intValue() == 1,
	    	            ((Number) data.get("hasLunch")).intValue() == 1,
	    	            ((Number) data.get("hasDinner")).intValue() == 1,
	    	            ((Number) data.get("hasSnack")).intValue() == 1
	    	        )
	    	    ));
	    
	    return new MonthlyDietSummaryResponseDto(dateStr, dietMap);
	}
	
/**
 * 식단 메인의 식단 정보 
 */
	@Override
	public DailyDietResponseDto getDailyDiet(String dateStr) {
	    try {
	        // 현재 사용자 ID 가져오기
	        int userNumber = userUtil.getCurrentUserNumber();
	        
	        // 파라미터 설정
	        Map<String, Object> params = new HashMap<>();
	        params.put("userNumber", userNumber);
	        params.put("date", dateStr);
	        
	        // DB에서 해당 날짜의 식단 상세 정보 조회
	        List<Map<String, Object>> dietDetails = dietDao.getDailyDietDetails(params);
	        
	        // 데이터가 없는 경우
	        if (dietDetails.isEmpty()) {
	            return new DailyDietResponseDto(true, 
	                new DailyDietDataDto(dateStr, 
	                    new NutritionDto(0, 0, 0, 0), 
	                    new HashMap<>()));
	        }
	        
	        // 식사별로 그룹화
	        Map<String, List<Map<String, Object>>> mealGroups = dietDetails.stream()
	            .collect(Collectors.groupingBy(data -> (String) data.get("meal_type")));
	        
	        // 일일 총 영양성분 계산
	        double totalCalories = 0;
	        double totalProtein = 0;
	        double totalFat = 0;
	        double totalCarbs = 0;
	        
	        // 식사별 데이터 구성
	        Map<String, MealDto> meals = new HashMap<>();
	        
	        for (Map.Entry<String, List<Map<String, Object>>> entry : mealGroups.entrySet()) {
	            String mealType = entry.getKey();
	            List<Map<String, Object>> mealFoods = entry.getValue();
	            
	            // 해당 식사의 영양성분 합계 계산
	            double mealCalories = 0;
	            double mealProtein = 0;
	            double mealFat = 0;
	            double mealCarbs = 0;
	            
	            // 식사 이미지 추출 (첫 번째 음식의 diet_image 사용)
	            String dietImage = null;
	            if (!mealFoods.isEmpty()) {
	                dietImage = prefix + (String) mealFoods.get(0).get("diet_image");
	            }
	            
	            // 음식 리스트 구성
	            List<FoodItemDto> foods = new ArrayList<>();
	            
	            for (Map<String, Object> foodData : mealFoods) {
	                // 실제 섭취량 기준 영양성분 (DB에서 계산됨)
	                double actualCalories = ((Number) foodData.get("actual_calories")).doubleValue();
	                double actualProtein = ((Number) foodData.get("actual_protein")).doubleValue();
	                double actualFat = ((Number) foodData.get("actual_fat")).doubleValue();
	                double actualCarbs = ((Number) foodData.get("actual_carbs")).doubleValue();
	                double actualSodium = ((Number) foodData.get("actual_sodium")).doubleValue();
	                double actualCholesterol = ((Number) foodData.get("actual_cholesterol")).doubleValue();
	                
	                // 식사별 합계에 더하기
	                mealCalories += actualCalories;
	                mealProtein += actualProtein;
	                mealFat += actualFat;
	                mealCarbs += actualCarbs;
	                
	                // 음식 아이템 생성 (추가 정보 포함)
	                FoodItemDto foodItem = new FoodItemDto(
	                    ((Number) foodData.get("food_id")).intValue(), // foodId 추가
	                    (String) foodData.get("food_name"),
	                    ((Number) foodData.get("diet_amount")).intValue(),
	                    (String) foodData.get("diet_unit"),
	                    Math.round(actualCalories * 100.0) / 100.0, // 소수점 2자리 반올림
	                    Math.round(actualProtein * 100.0) / 100.0,
	                    Math.round(actualFat * 100.0) / 100.0,
	                    Math.round(actualCarbs * 100.0) / 100.0,
	                    Math.round(actualSodium * 100.0) / 100.0, // 나트륨 추가
	                    Math.round(actualCholesterol * 100.0) / 100.0, // 콜레스테롤 추가
	                    ((Number) foodData.get("food_weight")).intValue() // 원래 1인분 중량 추가
	                );
	                
	                foods.add(foodItem);
	            }
	            
	            // 일일 총합에 더하기
	            totalCalories += mealCalories;
	            totalProtein += mealProtein;
	            totalFat += mealFat;
	            totalCarbs += mealCarbs;
	            
	            // 식사 DTO 생성 (dietImage 포함)
	            NutritionDto mealNutrition = new NutritionDto(
	                Math.round(mealCalories * 100.0) / 100.0,
	                Math.round(mealProtein * 100.0) / 100.0,
	                Math.round(mealFat * 100.0) / 100.0,
	                Math.round(mealCarbs * 100.0) / 100.0
	            );
	            
	            meals.put(mealType, new MealDto(mealNutrition, foods, dietImage));
	        }
	        
	        // 일일 총 영양성분 DTO 생성
	        NutritionDto dailyTotal = new NutritionDto(
	            Math.round(totalCalories * 100.0) / 100.0,
	            Math.round(totalProtein * 100.0) / 100.0,
	            Math.round(totalFat * 100.0) / 100.0,
	            Math.round(totalCarbs * 100.0) / 100.0
	        );
	        
	        // 최종 응답 DTO 구성
	        DailyDietDataDto data = new DailyDietDataDto(dateStr, dailyTotal, meals);
	        return new DailyDietResponseDto(true, data);
	        
	    } catch (Exception e) {
	        // 로그 출력
	        e.printStackTrace();
	        return new DailyDietResponseDto(false, null);
	    }
	}
//	public DailyDietResponseDto getDailyDiet(String dateStr) {
//	    try {
//	        // 현재 사용자 ID 가져오기
//	        int userNumber = userUtil.getCurrentUserNumber();
//	        
//	        // 파라미터 설정
//	        Map<String, Object> params = new HashMap<>();
//	        params.put("userNumber", userNumber);
//	        params.put("date", dateStr);
//	        
//	        // DB에서 해당 날짜의 식단 상세 정보 조회
//	        List<Map<String, Object>> dietDetails = dietDao.getDailyDietDetails(params);
//	        
//	        // 데이터가 없는 경우
//	        if (dietDetails.isEmpty()) {
//	            return new DailyDietResponseDto(true, 
//	                new DailyDietDataDto(dateStr, 
//	                    new NutritionDto(0, 0, 0, 0), 
//	                    new HashMap<>()));
//	        }
//	        
//	        // 식사별로 그룹화
//	        Map<String, List<Map<String, Object>>> mealGroups = dietDetails.stream()
//	            .collect(Collectors.groupingBy(data -> (String) data.get("meal_type")));
//	        
//	        // 일일 총 영양성분 계산
//	        double totalCalories = 0;
//	        double totalProtein = 0;
//	        double totalFat = 0;
//	        double totalCarbs = 0;
//	        
//	        // 식사별 데이터 구성
//	        Map<String, MealDto> meals = new HashMap<>();
//	        
//	        for (Map.Entry<String, List<Map<String, Object>>> entry : mealGroups.entrySet()) {
//	            String mealType = entry.getKey();
//	            List<Map<String, Object>> mealFoods = entry.getValue();
//	            
//	            // 해당 식사의 영양성분 합계 계산
//	            double mealCalories = 0;
//	            double mealProtein = 0;
//	            double mealFat = 0;
//	            double mealCarbs = 0;
//	            
//	            // 음식 리스트 구성
//	            List<FoodItemDto> foods = new ArrayList<>();
//	            
//	            for (Map<String, Object> foodData : mealFoods) {
//	                // 실제 섭취량 기준 영양성분 (DB에서 계산됨)
//	                double actualCalories = ((Number) foodData.get("actual_calories")).doubleValue();
//	                double actualProtein = ((Number) foodData.get("actual_protein")).doubleValue();
//	                double actualFat = ((Number) foodData.get("actual_fat")).doubleValue();
//	                double actualCarbs = ((Number) foodData.get("actual_carbs")).doubleValue();
//	                
//	                // 식사별 합계에 더하기
//	                mealCalories += actualCalories;
//	                mealProtein += actualProtein;
//	                mealFat += actualFat;
//	                mealCarbs += actualCarbs;
//	                
//	                // 음식 아이템 생성 (foodId, name, calories)
//	                FoodItemDto foodItem = new FoodItemDto(
//	                    ((Number) foodData.get("food_id")).intValue(),
//	                    (String) foodData.get("food_name"),
//	                    Math.round(actualCalories * 100.0) / 100.0 // 소수점 2자리 반올림
//	                );
//	                
//	                foods.add(foodItem);
//	            }
//	            
//	            // 일일 총합에 더하기
//	            totalCalories += mealCalories;
//	            totalProtein += mealProtein;
//	            totalFat += mealFat;
//	            totalCarbs += mealCarbs;
//	            
//	            // 식사 DTO 생성
//	            NutritionDto mealNutrition = new NutritionDto(
//	                Math.round(mealCalories * 100.0) / 100.0,
//	                Math.round(mealProtein * 100.0) / 100.0,
//	                Math.round(mealFat * 100.0) / 100.0,
//	                Math.round(mealCarbs * 100.0) / 100.0
//	            );
//	            
//	            meals.put(mealType, new MealDto(mealNutrition, foods));
//	        }
//	        
//	        // 일일 총 영양성분 DTO 생성
//	        NutritionDto dailyTotal = new NutritionDto(
//	            Math.round(totalCalories * 100.0) / 100.0,
//	            Math.round(totalProtein * 100.0) / 100.0,
//	            Math.round(totalFat * 100.0) / 100.0,
//	            Math.round(totalCarbs * 100.0) / 100.0
//	        );
//	        
//	        // 최종 응답 DTO 구성
//	        DailyDietDataDto data = new DailyDietDataDto(dateStr, dailyTotal, meals);
//	        return new DailyDietResponseDto(true, data);
//	        
//	    } catch (Exception e) {
//	        // 로그 출력
//	        e.printStackTrace();
//	        return new DailyDietResponseDto(false, null);
//	    }
//	}

	@Override
	public FoodDetailResponseDto getFoodDetailByDate(String dateStr, int foodId) {
	    try {
	        // 현재 사용자 ID 가져오기
	        int userNumber = userUtil.getCurrentUserNumber();
	        
	        // 파라미터 설정
	        Map<String, Object> params = new HashMap<>();
	        params.put("userNumber", userNumber);
	        params.put("date", dateStr);
	        params.put("foodId", foodId);
	        
	        // DB에서 해당 음식의 상세 정보 조회
	        Map<String, Object> foodDetailData = dietDao.getFoodDetailByDate(params);
	        
	        // 데이터가 없는 경우
	        if (foodDetailData == null || foodDetailData.isEmpty()) {
	            return new FoodDetailResponseDto(false, null);
	        }
	        
	        // 실제 섭취량 기준 영양성분 계산 (이미 DB에서 계산됨)
	        double actualCalories = ((Number) foodDetailData.get("actual_calories")).doubleValue();
	        double actualProtein = ((Number) foodDetailData.get("actual_protein")).doubleValue();
	        double actualFat = ((Number) foodDetailData.get("actual_fat")).doubleValue();
	        double actualCarbs = ((Number) foodDetailData.get("actual_carbs")).doubleValue();
	        double actualSodium = ((Number) foodDetailData.get("actual_sodium")).doubleValue();
	        double actualCholesterol = ((Number) foodDetailData.get("actual_cholesterol")).doubleValue();
	        
	        // DTO 생성
	        FoodDetailDto foodDetail = new FoodDetailDto(
	            ((Number) foodDetailData.get("food_id")).intValue(),
	            (String) foodDetailData.get("food_name"),
	            ((Number) foodDetailData.get("diet_amount")).intValue(),
	            (String) foodDetailData.get("diet_unit"),
	            Math.round(actualCalories * 100.0) / 100.0,
	            Math.round(actualProtein * 100.0) / 100.0,
	            Math.round(actualFat * 100.0) / 100.0,
	            Math.round(actualCarbs * 100.0) / 100.0,
	            Math.round(actualSodium * 100.0) / 100.0,
	            Math.round(actualCholesterol * 100.0) / 100.0,
	            ((Number) foodDetailData.get("base_weight")).intValue()
	        );
	        
	        return new FoodDetailResponseDto(true, foodDetail);
	        
	    } catch (Exception e) {
	        // 로그 출력
	        e.printStackTrace();
	        return new FoodDetailResponseDto(false, null);
	    }
	}
	

	/**
	 *s3에 이미지 저장 
	 */
	@Override
	public String imageUpload(MultipartFile file) throws IOException {
        // 파일명 생성
        String originalFileName = file.getOriginalFilename();
        String ext = originalFileName.substring(originalFileName.lastIndexOf("."));
        String s3Key = UUID.randomUUID() + ext;
        
        // 직접 S3에 업로드 (로컬 저장 없이)
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(file.getContentType());
        metadata.setContentLength(file.getSize());
        
        s3Config.amazonS3Client().putObject(new PutObjectRequest(bucket, s3Key, file.getInputStream(), metadata)
                .withCannedAcl(CannedAccessControlList.PublicRead));
        
        // S3 키만 반환 (전체 URL 대신)
        return prefix+s3Key;
    }

	
    
    /**
     * ChatGPT API 호출 (Map 버전)
     */
    public Map<String, Object> promptWithMap(Map<String, Object> requestMap) {
        System.out.println("[+] ChatGPT API 호출을 시작합니다.");
        
        Map<String, Object> resultMap = new HashMap<>();
        
        try {
            // 헤더 설정
            HttpHeaders headers = chatGPTConfig.httpHeaders();
            
            // HTTP 요청 엔티티 구성
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestMap, headers);
            
            // API 호출
            ResponseEntity<String> response = chatGPTConfig
                    .restTemplate()
                    .exchange(promptUrl, HttpMethod.POST, requestEntity, String.class);
            
            System.out.println("ChatGPT API 응답 상태: " + response.getStatusCode());
            System.out.println("ChatGPT API 응답 본문: " + response.getBody());
            
            // JSON 파싱
            ObjectMapper om = new ObjectMapper();
            resultMap = om.readValue(response.getBody(), new TypeReference<Map<String, Object>>() {});
            
            System.out.println("[+] ChatGPT API 호출이 성공적으로 완료되었습니다.");
            
        } catch (Exception e) {
            System.err.println("ChatGPT API 호출 오류: " + e.getMessage());
            e.printStackTrace();
            resultMap.put("error", "API 호출 중 오류가 발생했습니다: " + e.getMessage());
        }
        
        return resultMap;
    }
    
    /**
     * 이미지 업로드 + AI 분석 통합 처리 (Map 버전)
     */
    @Override
    public Map<String, Object> analyzeFood(MultipartFile file) throws Exception {
        Map<String, Object> result = new HashMap<>();
        
        try {
        	 System.out.println("[+] 음식 분석을 시작합니다.");
             
             // 1. 이미지 업로드
             String imageUrl = imageUpload(file);
             System.out.println("이미지 업로드 완료: " + imageUrl);
             
             // 2. GPT 분석 요청
             Map<String, Object> chatRequestMap = createChatRequestMap(imageUrl);
             Map<String, Object> aiResponse = promptWithMap(chatRequestMap);
             
             if (aiResponse.containsKey("error")) {
                 throw new RuntimeException("AI 분석 중 오류 발생: " + aiResponse.get("error"));
             }
             
             // 3. GPT 응답에서 음식 정보 추출
             String analysisText = extractAnalysisFromResponse(aiResponse);
             System.out.println("GPT 분석 결과: " + analysisText);
             
             // 4. JSON 파싱해서 음식 목록 추출
             List<AnalyzedFoodDto> analyzedFoods = parseGptResponse(analysisText);
             System.out.println("4. JSON 파싱 : " + analyzedFoods);
             
             // 5. DB에서 영양정보 조회 및 계산
             List<FoodNutritionInfoDto> nutritionInfos = getNutritionInfos(analyzedFoods);
             System.out.println("5. DB에서 영양정보 조회 및 계 : " + nutritionInfos);
             
             // 6. 결과 구성
             result.put("success", true);
             result.put("imageUrl", imageUrl);
//             result.put("analyzedFoods", analyzedFoods);
             result.put("nutritionInfos", nutritionInfos);
//             result.put("totalCalories", calculateTotalCalories(nutritionInfos));
             
             System.out.println("[+] 음식 분석이 성공적으로 완료되었습니다.");
             return result;
             
         } catch (Exception e) {
             System.err.println("음식 분석 중 오류 발생: " + e.getMessage());
             e.printStackTrace();
             throw e;
         }
     }
    
    /**
     * GPT 응답을 파싱해서 음식 목록 추출
     */
    private List<AnalyzedFoodDto> parseGptResponse(String gptResponse) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            
            // JSON에서 불필요한 부분 제거 (GPT가 추가 설명을 넣을 수 있음)
            String cleanJson = extractJsonFromResponse(gptResponse);
            
            FoodAnalysisResponseDto response = mapper.readValue(cleanJson, FoodAnalysisResponseDto.class);
            return response.getFoods();
            
        } catch (Exception e) {
            System.err.println("GPT 응답 파싱 오류: " + e.getMessage());
            System.err.println("응답 내용: " + gptResponse);
            
            // 파싱 실패 시 빈 리스트 반환
            return new ArrayList<>();
        }
    }
    
    /**
     * ChatGPT 요청 객체 생성 (Map 사용)
     */
    private Map<String, Object> createChatRequestMap(String imageUrl) {
        System.out.println("ChatGPT 요청 생성 중 - 이미지 URL: " + imageUrl);
        
        try {
            // 텍스트 컨텐츠
            Map<String, Object> textContent = new HashMap<>();
            textContent.put("type", "text");
            textContent.put("text", FIXED_PROMPT);
            
            // 이미지 URL 컨텐츠
            Map<String, Object> imageUrlMap = new HashMap<>();
            imageUrlMap.put("url", imageUrl);
            
            Map<String, Object> imageContent = new HashMap<>();
            imageContent.put("type", "image_url");
            imageContent.put("image_url", imageUrlMap);
            
            // 컨텐츠 리스트
            List<Map<String, Object>> contents = Arrays.asList(textContent, imageContent);
            
            // 메시지
            Map<String, Object> message = new HashMap<>();
            message.put("role", "user");
            message.put("content", contents);
            
            // 최종 요청
            Map<String, Object> request = new HashMap<>();
            request.put("model", "gpt-4o");
            request.put("messages", Arrays.asList(message));
            request.put("max_tokens", 1000);
            
            // 디버깅용 출력
            ObjectMapper mapper = new ObjectMapper();
            String jsonString = mapper.writeValueAsString(request);
            System.out.println("생성된 JSON: " + jsonString);
            
            return request;
            
        } catch (Exception e) {
            System.err.println("ChatGPT 요청 생성 중 오류: " + e.getMessage());
            throw new RuntimeException("ChatGPT 요청 생성 실패", e);
        }
    }
    
    
    
    /**
     * AI 응답에서 실제 분석 텍스트 추출
     */
    private String extractAnalysisFromResponse(Map<String, Object> aiResponse) {
        try {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> choices = (List<Map<String, Object>>) aiResponse.get("choices");
            
            if (choices != null && !choices.isEmpty()) {
                @SuppressWarnings("unchecked")
                Map<String, Object> firstChoice = choices.get(0);
                @SuppressWarnings("unchecked")
                Map<String, Object> message = (Map<String, Object>) firstChoice.get("message");
                
                if (message != null) {
                    return (String) message.get("content");
                }
            }
        } catch (Exception e) {
            System.err.println("AI 응답 파싱 중 오류: " + e.getMessage());
        }
        return null;
    }
	
    /**
     * 응답에서 JSON 부분만 추출
     */
    private String extractJsonFromResponse(String response) {
        if (response == null) return "{}";
        
        // JSON 시작과 끝 찾기
        int startIndex = response.indexOf("{");
        int lastIndex = response.lastIndexOf("}");
        
        if (startIndex != -1 && lastIndex != -1 && lastIndex > startIndex) {
            return response.substring(startIndex, lastIndex + 1);
        }
        
        return response;
    }
    
    /**
     * 분석된 음식들의 영양정보를 DB에서 조회하고 중량에 맞게 계산
     */
    private List<FoodNutritionInfoDto> getNutritionInfos(List<AnalyzedFoodDto> analyzedFoods) {
        List<FoodNutritionInfoDto> nutritionInfos = new ArrayList<>();
        
        for (AnalyzedFoodDto analyzedFood : analyzedFoods) {
        	System.out.println("현재 음식 : " + analyzedFood);
            // DB에서 음식 검색
            List<FoodDto> foundFoods = dietDao.findFoodsByName(analyzedFood.getName());
            
            if (!foundFoods.isEmpty()) {
                FoodDto food = foundFoods.get(0); // 가장 유사한 음식 선택
                
                // 중량 비율 계산 (GPT가 준 중량 / 100g)
                // 예: GPT가 300g이라고 했으면 → 300 / 100 = 3.0배
                double ratio = (double) analyzedFood.getWeight() / 100.0;
                
                FoodNutritionInfoDto nutritionInfo = new FoodNutritionInfoDto();
                nutritionInfo.setFoodId(food.getFoodId());
                nutritionInfo.setFoodName(food.getFoodName());
                
                // DB의 원본 값 (100g 기준)
                nutritionInfo.setFoodKcal(food.getFoodKcal());
                nutritionInfo.setFoodCarbs(food.getFoodCarbs());
                nutritionInfo.setFoodProtein(food.getFoodProtein());
                nutritionInfo.setFoodFat(food.getFoodFat());
                nutritionInfo.setFoodSodium(food.getFoodSodium());
                nutritionInfo.setFoodCholesterol(food.getFoodCholesterol());
                nutritionInfo.setFoodWeight(food.getFoodWeight());
                nutritionInfo.setAnalyzedWeight(analyzedFood.getWeight());
                
                // GPT가 분석한 중량에 맞게 영양정보 계산 (100g 기준 × 비율)
                nutritionInfo.setCalculatedKcal(Math.round(food.getFoodKcal() * ratio * 100.0) / 100.0);
                nutritionInfo.setCalculatedCarbs(Math.round(food.getFoodCarbs() * ratio * 100.0) / 100.0);
                nutritionInfo.setCalculatedProtein(Math.round(food.getFoodProtein() * ratio * 100.0) / 100.0);
                nutritionInfo.setCalculatedFat(Math.round(food.getFoodFat() * ratio * 100.0) / 100.0);
                nutritionInfo.setCalculatedSodium(Math.round(food.getFoodSodium() * ratio * 100.0) / 100.0);
                nutritionInfo.setCalculatedCholesterol(Math.round(food.getFoodCholesterol() * ratio * 100.0) / 100.0);
                
                nutritionInfos.add(nutritionInfo);
                
                System.out.println("========== 영양정보 계산 결과 ==========");
                System.out.println("음식명: " + analyzedFood.getName() + " → " + food.getFoodName());
                System.out.println("GPT 분석 중량: " + analyzedFood.getWeight() + "g");
                System.out.println("계산 비율: " + analyzedFood.getWeight() + "g ÷ 100g = " + ratio);
                System.out.println("칼로리: " + food.getFoodKcal() + "kcal (100g) → " + nutritionInfo.getCalculatedKcal() + "kcal (" + analyzedFood.getWeight() + "g)");
                System.out.println("단백질: " + food.getFoodProtein() + "g (100g) → " + nutritionInfo.getCalculatedProtein() + "g (" + analyzedFood.getWeight() + "g)");
                System.out.println("탄수화물: " + food.getFoodCarbs() + "g (100g) → " + nutritionInfo.getCalculatedCarbs() + "g (" + analyzedFood.getWeight() + "g)");
                System.out.println("지방: " + food.getFoodFat() + "g (100g) → " + nutritionInfo.getCalculatedFat() + "g (" + analyzedFood.getWeight() + "g)");
                System.out.println("나트륨: " + food.getFoodSodium() + "mg (100g) → " + nutritionInfo.getCalculatedSodium() + "mg (" + analyzedFood.getWeight() + "g)");
                System.out.println("콜레스테롤: " + food.getFoodCholesterol() + "mg (100g) → " + nutritionInfo.getCalculatedCholesterol() + "mg (" + analyzedFood.getWeight() + "g)");
                System.out.println("=======================================");
                
            } else {
                System.out.println("매칭 실패: " + analyzedFood.getName() + " - DB에서 찾을 수 없음");
            }
        }
        
        return nutritionInfos;
    }
    
    /**
     * 총 칼로리 계산
     */
    private double calculateTotalCalories(List<FoodNutritionInfoDto> nutritionInfos) {
        return nutritionInfos.stream()
                .mapToDouble(FoodNutritionInfoDto::getCalculatedKcal)
                .sum();
    }

    
	

}
