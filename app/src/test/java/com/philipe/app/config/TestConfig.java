package com.philipe.app.config;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;

@TestConfiguration
@SpringBootTest(properties = "spring.shell.interactive.enabled=false")
@ActiveProfiles("test")
@Configuration
public class TestConfig {
    
}
