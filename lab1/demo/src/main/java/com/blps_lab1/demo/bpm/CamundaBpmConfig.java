package com.blps_lab1.demo.bpm;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(CamundaProperties.class)
public class CamundaBpmConfig {
}
