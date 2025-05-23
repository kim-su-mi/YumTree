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
import com.fasterxml.jackson.core.JsonProcessingException;
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
	private final String FIXED_PROMPT = "이 음식 사진을 분석해서 다음 정보를 JSON 형태로 제공해주세요:\n" +
            "1. 음식 이름\n" +
            "2. 예상 칼로리 (kcal)\n" +
            "3. 주요 영양성분 (단백질, 탄수화물, 지방)\n" +
            "4. 건강도 평가 (1-10점)\n" +
            "5. 식단 개선 제안\n\n" +
            "다음과 같은 JSON 형식으로 답변해주세요:\n" +
            "{\n" +
            "  \"foodName\": \"음식이름\",\n" +
            "  \"calories\": 숫자,\n" +
            "  \"protein\": \"단백질g\",\n" +
            "  \"carbohydrate\": \"탄수화물g\",\n" +
            "  \"fat\": \"지방g\",\n" +
            "  \"healthScore\": 숫자,\n" +
            "  \"suggestions\": \"개선제안\"\n" +
            "}";

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
//	public String imageUpload(MultipartRequest request) throws IOException {
//		// request에서 이미지 파일을 뽑아냄 
//		MultipartFile file = request.getFile("upload");
//
//		// 뽑아낸 이미지 파일에서 이름 및 확장자 추출 
//        String fileName = file.getOriginalFilename();
//        String ext = fileName.substring(fileName.indexOf("."));
//
//        // 이미지 파일 이름 유일성을 위해 uuid적용 
//        String uuidFileName = UUID.randomUUID() + ext;
//        
//        //서버환경에 저장할 경로 생성 
//        String localPath = localLocation + uuidFileName;
//
//        //서버환경에 이미지 파일 저장 
//        File localFile = new File(localPath);
//        file.transferTo(localFile);
//
//
//        // s3에 이미지 저장 
//        s3Config.amazonS3Client().putObject(new PutObjectRequest(bucket, uuidFileName, localFile).withCannedAcl(CannedAccessControlList.PublicRead));
//        // 이미지가 올라간 s3의 주소 
//        String s3Url = s3Config.amazonS3Client().getUrl(bucket, uuidFileName).toString();
//       System.out.println("s3  : "+s3Url);
//        
//        //서버에 저장한 이미지 삭제 
//        localFile.delete();
//
//        return s3Url;
//	}
	
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
     * ChatGPT API 호출
     */
    @Override
    public Map<String, Object> prompt(ChatCompletionDto chatCompletionDto) {
        System.out.println("[+] ChatGPT API 호출을 시작합니다.");
        
        Map<String, Object> resultMap = new HashMap<>();
        
        try {
            // [STEP1] 토큰 정보가 포함된 Header를 가져옵니다.
            HttpHeaders headers = chatGPTConfig.httpHeaders();
            
            // [STEP2] HTTP 요청 엔티티 구성
            HttpEntity<ChatCompletionDto> requestEntity = new HttpEntity<>(chatCompletionDto, headers);
            
            // [STEP3] RestTemplate을 사용해서 API 호출
            ResponseEntity<String> response = chatGPTConfig
                    .restTemplate()
                    .exchange(promptUrl, HttpMethod.POST, requestEntity, String.class);
            
            System.out.println("ChatGPT API 응답 상태: " + response.getStatusCode());
            System.out.println("ChatGPT API 응답 본문: " + response.getBody());
            
            // [STEP4] String -> HashMap 역직렬화
            ObjectMapper om = new ObjectMapper();
            resultMap = om.readValue(response.getBody(), new TypeReference<Map<String, Object>>() {});
            
            System.out.println("[+] ChatGPT API 호출이 성공적으로 완료되었습니다.");
            
        } catch (JsonProcessingException e) {
            System.err.println("JSON 파싱 오류: " + e.getMessage());
            e.printStackTrace();
            resultMap.put("error", "JSON 파싱 중 오류가 발생했습니다: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("ChatGPT API 호출 오류: " + e.getMessage());
            e.printStackTrace();
            resultMap.put("error", "API 호출 중 오류가 발생했습니다: " + e.getMessage());
        }
        
        return resultMap;
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
            
            // 2. ChatGPT 요청 구성 (Map 사용)
            Map<String, Object> chatRequestMap = createChatRequestMap(imageUrl);
            
            // 3. AI 분석 요청 (Map 버전 사용)
            Map<String, Object> aiResponse = promptWithMap(chatRequestMap);
            
            // 4. 오류 체크
            if (aiResponse.containsKey("error")) {
                throw new RuntimeException("AI 분석 중 오류 발생: " + aiResponse.get("error"));
            }
            
            // 5. 결과 구성
            result.put("imageUrl", imageUrl);
            result.put("aiAnalysis", aiResponse);
            
            // AI 응답에서 실제 분석 결과 추출
            String analysisText = extractAnalysisFromResponse(aiResponse);
            if (analysisText != null) {
                result.put("analysisText", analysisText);
            }
            
            System.out.println("[+] 음식 분석이 성공적으로 완료되었습니다.");
            return result;
            
        } catch (Exception e) {
            System.err.println("음식 분석 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
            throw e;
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
	
	

}
