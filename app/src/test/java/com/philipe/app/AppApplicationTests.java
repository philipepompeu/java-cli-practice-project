package com.philipe.app;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.context.annotation.Import;

import com.philipe.app.config.TestConfig;

@Import(TestConfig.class) 
@SpringBootTest
class AppApplicationTests {

	@Test
	void contextLoads() {
	}

}
