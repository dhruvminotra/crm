package com.veyora.crm.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/** CORS lives in SecurityConfig.corsConfigurationSource (single source of truth). */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
}
