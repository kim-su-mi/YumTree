package com.ssafy.yumTree.diet;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartRequest;

import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.ssafy.yumTree.config.S3Config;
import com.ssafy.yumTree.jwt.UserUtil;



@Service
public class DietServiceImpl implements DietService{
	private final DietDao dietDao;
	private final UserUtil userUtil;
	private S3Config s3Config;
	
	public DietServiceImpl(DietDao dietDao,UserUtil userUtil,S3Config s3Config) {
		this.dietDao = dietDao;
		this.userUtil = userUtil;
		this.s3Config = s3Config;
	}

	@Override
	public int addFood(FoodDto foodDto) {
		
		return dietDao.insertFood(foodDto);
	}
	
	@Value("${cloud.aws.s3.bucket}")
    private String bucket;
	@Value("${cloud.aws.s3.url.prefix}")
	private String prefix;

    private String localLocation = "C:\\ssafy\\img\\";


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

	
	

}
