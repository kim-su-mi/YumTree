package com.ssafy.yumTree.diet;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ContentDto {
	 private String type;
	    private String text; // type이 "text"일 때만 사용
	    @JsonProperty("image_url") // 이 부분이 중요!
	    private ImageUrlDto imageUrl; // type이 "image_url"일 때만 사용
    
    
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public ImageUrlDto getImageUrl() {
		return imageUrl;
	}
	public void setImageUrl(ImageUrlDto imageUrl) {
		this.imageUrl = imageUrl;
	}
    
    
    
}
