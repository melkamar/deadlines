package com.melkamar.deadlines.config;

import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.security.crypto.keygen.StringKeyGenerator;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

import javax.servlet.Filter;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 25.03.2016 14:07
 */
@Configuration
@EnableScheduling
public class Beans {
    @Bean
    public ShaPasswordEncoder passwordEncoder() {
        return new ShaPasswordEncoder();
    }

    @Bean
    public StringKeyGenerator randomStringGenerator() {
        return org.springframework.security.crypto.keygen.KeyGenerators.string();
    }

    @Bean
    public Filter logFilter(){
        CommonsRequestLoggingFilter filter = new CommonsRequestLoggingFilter();
        filter.setIncludeQueryString(true);
        filter.setIncludePayload(true);
        return filter;
    }
}
