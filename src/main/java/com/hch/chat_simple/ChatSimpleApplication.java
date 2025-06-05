package com.hch.chat_simple;

import com.github.pagehelper.page.PageAutoDialect;

// import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.github.xiaoymin.knife4j.spring.annotations.EnableKnife4j;
import com.hch.chat_simple.config.LogicSqlDialect;


@EnableKnife4j
@SpringBootApplication
// @MapperScan(value = {"com.hch.chat_simple.mapper"})
public class ChatSimpleApplication {

	public static void main(String[] args) {
		// MybatisMapperAnnotationBuilder
		PageAutoDialect.registerDialectAlias("mysql", LogicSqlDialect.class);
		SpringApplication.run(ChatSimpleApplication.class, args);
	}

}
