package com.kshitij.IntervuLog.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private ProfileCompletionInterceptor profileCompletionInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(profileCompletionInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/dashboard",
                        "/userForm",
                        "/saveUser",
                        "/logout",
                        "/css/**", "/js/**", "/images/**",
                        "/favicon.ico", "/icon.png"
                );
    }
}

