package com.uvt.newcomerassistan.demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.uvt.newcomerassistant.demo.UvtNewcomerAssistantApplication;

@SpringBootTest(classes = UvtNewcomerAssistantApplication.class)
@ActiveProfiles("h2")
class DemoApplicationTests {

	@Test
	void contextLoads() {
	}

}
