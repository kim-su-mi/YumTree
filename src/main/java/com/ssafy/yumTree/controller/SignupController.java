package com.ssafy.yumTree.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ssafy.yumTree.model.dto.UserDto;
import com.ssafy.yumTree.model.service.SignupService;

@RestController
public class SignupController {
	private final SignupService signupService;

    public SignupController(SignupService signupService) {
        
        this.signupService = signupService;
    }

    @PostMapping("/signup")
    public String signupProcess(@RequestBody UserDto userDto) {
    System.out.println(userDto);
        System.out.println( "userName : "+userDto.getUserName());
        signupService.signupProcess(userDto);

        return "ok";
    }

}
