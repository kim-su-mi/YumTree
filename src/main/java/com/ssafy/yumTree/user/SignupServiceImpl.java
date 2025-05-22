package com.ssafy.yumTree.user;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class SignupServiceImpl implements SignupService {
	private final UserDao userDao;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public SignupServiceImpl(UserDao signupDao, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userDao = signupDao;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    //회원가입 
    public void signupProcess(UserDto userDto) {

        String userId = userDto.getUserId();

        System.out.println(111111111);
        int isExist = userDao.existsByUserId(userId);
        System.out.println(222222222);

        if (isExist > 0) {
        	System.out.println("중복 ID");
            return;
        }

//        UserDto user = new UserDto();
//        user.setUserId(userId);
//        user.setUserPw(bCryptPasswordEncoder.encode(password));
//        user.setUserName(userDto.getUserName());
//        user.setUserBirth(userDto);

//        user.setUserRole("ROLE_ADMIN");
        userDto.setUserPw(bCryptPasswordEncoder.encode(userDto.getUserPw()));
//        userDto.setUserRole("ROLE_ADMIN");
        userDto.setUserRole("ROLE_USER");
        
        userDto.setUserTargetCalories(calculateCal(userDto.getUserHeight(),userDto.getUserWeight(),userDto.getUserActivityLevel()));

        System.out.println(userDto);
        
        
        userDao.insertUser(userDto);
    }
    
    // 권장 칼로리 계산
    public int calculateCal(int height,int weight,int activity) {
    	
    	return (int)Math.round(((height-100)*0.9)*activity);
    }

}
