package com.ssafy.yumTree.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan(basePackages = "com.ssafy.yumTree.model.dao")
public class DBConfig {

}
