package com.ssafy.yumTree.diet;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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

	@Override
	public MonthlyDietSummaryResponseDto getMonthlySummary(String dateStr) {
		// 현재 사용자 ID 가져오기
        int userNumber = userUtil.getCurrentUserNumber();
        
        // 날짜 파싱
        LocalDate date = LocalDate.parse(dateStr);
        LocalDate firstDayOfMonth = date.withDayOfMonth(1);
        LocalDate lastDayOfMonth = date.withDayOfMonth(date.lengthOfMonth());
        
        Map<String, Object> map = new HashMap<>();
        map.put("userNumber", userNumber);
        map.put("firstDayOfMonth", firstDayOfMonth);
        map.put("lastDayOfMonth", lastDayOfMonth);
        
        // 월간 데이터 조회
        List<Map<String, Object>> monthlyData = dietDao.getDietSummaryByMonth(map);
        
        // DTO로 변환
        MonthlyDietSummaryResponseDto response = new MonthlyDietSummaryResponseDto();
        response.setDate(dateStr);
        
        List<DailyDietSummaryDto> dailySummaries = new ArrayList<>();
        
        // 해당 월의 모든 날짜에 대해 정보 구성
        for (int day = 1; day <= lastDayOfMonth.getDayOfMonth(); day++) {
            LocalDate currentDate = date.withDayOfMonth(day);
            String currentDateStr = currentDate.toString();
            
            DailyDietSummaryDto dailySummary = new DailyDietSummaryDto();
            dailySummary.setDate(currentDateStr);
            
            // 등록된 식사 정보가 있으면 설정
            for (Map<String, Object> data : monthlyData) {
                String dbDate = (String) data.get("diet_log_date");
                if (currentDateStr.equals(dbDate)) {
                    String mealTypes = (String) data.get("registered_meal_types");
                    dailySummary.setHasBreakfast(mealTypes.contains("아침"));
                    dailySummary.setHasLunch(mealTypes.contains("점심"));
                    dailySummary.setHasDinner(mealTypes.contains("저녁"));
                    dailySummary.setHasSnack(mealTypes.contains("간식"));
                    break;
                }
            }
            
            dailySummaries.add(dailySummary);
        }
        
        response.setDailySummaries(dailySummaries);
        return response;
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
             result.put("analyzedFoods", analyzedFoods);
             result.put("nutritionInfos", nutritionInfos);
             result.put("totalCalories", calculateTotalCalories(nutritionInfos));
             
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
