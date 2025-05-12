package com.ssafy.yumTree.user;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class SignupServiceImpl implements SignupService {
	private final SignupDao signupDao;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public SignupServiceImpl(SignupDao signupDao, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.signupDao = signupDao;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    //회원가입 
    public void signupProcess(UserDto userDto) {

        String userId = userDto.getUserId();

        int isExist = signupDao.existsByUserId(userId);

        if (isExist > 0) {

            return;
        }

//        UserDto user = new UserDto();
//        user.setUserId(userId);
//        user.setUserPw(bCryptPasswordEncoder.encode(password));
//        user.setUserName(userDto.getUserName());
//        user.setUserBirth(userDto);

//        user.setUserRole("ROLE_ADMIN");
        userDto.setUserPw(bCryptPasswordEncoder.encode(userDto.getUserPw()));
        userDto.setUserRole("ROLE_USER");

        System.out.println("1 : "+userDto);
        
        
        signupDao.insertUser(userDto);
    }

}
