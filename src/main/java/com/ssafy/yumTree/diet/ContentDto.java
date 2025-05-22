package com.ssafy.yumTree.diet;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ContentDto {
    private String type; // "text" 또는 "image_url"
    private String text; // type이 "text"일 때
    @JsonProperty("image_url")
    private ImageUrlDto imageUrl; // type이 "image_url"일 때
}
