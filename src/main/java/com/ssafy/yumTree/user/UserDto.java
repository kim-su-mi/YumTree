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
	private String userRole;
	private int userHeight;
	private int userWeight;
	private int userActivityLevel;
	private int userTargetCalories;
	private int userMileage;
	
	public UserDto() {
		// TODO Auto-generated constructor stub
	}

	public UserDto(int userNumber, String userId, String userPw, String userName, String userGender, Date userBirth,
			String userRole, int userHeight, int userWeight, int userActivityLevel, int userTargetCalories,
			int userMileage) {
		super();
		this.userNumber = userNumber;
		this.userId = userId;
		this.userPw = userPw;
		this.userName = userName;
		this.userGender = userGender;
		this.userBirth = userBirth;
		this.userRole = userRole;
		this.userHeight = userHeight;
		this.userWeight = userWeight;
		this.userActivityLevel = userActivityLevel;
		this.userTargetCalories = userTargetCalories;
		this.userMileage = userMileage;
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

	public String getUserRole() {
		return userRole;
	}

	public void setUserRole(String userRole) {
		this.userRole = userRole;
	}

	public int getUserHeight() {
		return userHeight;
	}

	public void setUserHeight(int userHeight) {
		this.userHeight = userHeight;
	}

	public int getUserWeight() {
		return userWeight;
	}

	public void setUserWeight(int userWeight) {
		this.userWeight = userWeight;
	}

	public int getUserActivityLevel() {
		return userActivityLevel;
	}

	public void setUserActivityLevel(int userActivityLevel) {
		this.userActivityLevel = userActivityLevel;
	}

	public int getUserTargetCalories() {
		return userTargetCalories;
	}

	public void setUserTargetCalories(int userTargetCalories) {
		this.userTargetCalories = userTargetCalories;
	}

	public int getUserMileage() {
		return userMileage;
	}

	public void setUserMileage(int userMileage) {
		this.userMileage = userMileage;
	}

	@Override
	public String toString() {
		return "UserDto [userNumber=" + userNumber + ", userId=" + userId + ", userPw=" + userPw + ", userName="
				+ userName + ", userGender=" + userGender + ", userBirth=" + userBirth + ", userRole=" + userRole
				+ ", userHeight=" + userHeight + ", userWeight=" + userWeight + ", userActivity_Level="
				+ userActivityLevel + ", userTargetCalories=" + userTargetCalories + ", userMileage=" + userMileage
				+ "]";
	}
	
	
	

}
