package com.ssafy.yumTree.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefreshDto {


    private Long id;
    private String userId; //어떤 유저에 대한 토큰인지 
    private String refresh;
    private String expiration;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getRefresh() {
		return refresh;
	}
	public void setRefresh(String refresh) {
		this.refresh = refresh;
	}
	public String getExpiration() {
		return expiration;
	}
	public void setExpiration(String expiration) {
		this.expiration = expiration;
	}
	@Override
	public String toString() {
		return "RefreshDto [id=" + id + ", userId=" + userId + ", refresh=" + refresh + ", expiration=" + expiration
				+ "]";
	}
    
    
}
