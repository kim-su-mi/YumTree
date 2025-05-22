package com.ssafy.yumTree.diet;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartRequest;

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
	public String imageUpload(MultipartRequest request) {
		
		return null;
	}

	
	

}
