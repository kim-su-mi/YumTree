package com.ssafy.yumTree.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan(
		basePackages = "com.ssafy.yumTree",
		annotationClass = org.apache.ibatis.annotations.Mapper.class)
public class DBConfig {
 
}
