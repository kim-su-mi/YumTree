package com.ssafy.yumTree.user;


import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class UserDto {
	private int userNumber;
	private String userId;
	private String userPw;
	private String userName;
	private String userGender;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private Date userBirth;
	private int userDiseaseNumber;
	private String userRole;
	
	public UserDto() {
		// TODO Auto-generated constructor stub
	}
	public UserDto(int userNumber, String userId, String userPw, String userName, String userGender, Date userBirth,
			int userDiseaseNumber, String userRole) {
		super();
		this.userNumber = userNumber;
		this.userId = userId;
		this.userPw = userPw;
		this.userName = userName;
		this.userGender = userGender;
		this.userBirth = userBirth;
		this.userDiseaseNumber = userDiseaseNumber;
		this.userRole = userRole;
	}
	
	
	public int getUserNumber() {
		return userNumber;
	}
	public void setUserNumber(int userNumber) {
		this.userNumber = userNumber;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getUserPw() {
		return userPw;
	}
	public void setUserPw(String userPw) {
		this.userPw = userPw;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getUserGender() {
		return userGender;
	}
	public void setUserGender(String userGender) {
		this.userGender = userGender;
	}
	public Date getUserBirth() {
		return userBirth;
	}
	public void setUserBirth(Date userBirth) {
		this.userBirth = userBirth;
	}
	public int getUserDiseaseNumber() {
		return userDiseaseNumber;
	}
	public void setUserDiseaseNumber(int userDiseaseNumber) {
		this.userDiseaseNumber = userDiseaseNumber;
	}
	public String getUserRole() {
		return userRole;
	}
	public void setUserRole(String userRole) {
		this.userRole = userRole;
	}
	@Override
	public String toString() {
		return "UserDto [userNumber=" + userNumber + ", userId=" + userId + ", userPw=" + userPw + ", userName="
				+ userName + ", userGender=" + userGender + ", userBirth=" + userBirth + ", userDiseaseNumber="
				+ userDiseaseNumber + ", userRole=" + userRole + "]";
	}
	
	
	
	

}
