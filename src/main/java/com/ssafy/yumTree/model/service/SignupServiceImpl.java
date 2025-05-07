package com.ssafy.yumTree.model.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.ssafy.yumTree.model.dao.SignupDao;
import com.ssafy.yumTree.model.dto.LoginDto;
import com.ssafy.yumTree.model.dto.UserDto;

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
        userDto.setUserRole("ROLE_ADMIN");

        System.out.println(userDto);
        
        
        signupDao.insertUser(userDto);
    }

}
