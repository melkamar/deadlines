package com.melkamar.deadlines.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.security.crypto.keygen.StringKeyGenerator;

/**
 * Created by Martin Melka (martin.melka@gmail.com)
 * 25.03.2016 14:07
 */
@Configuration
public class BeansConfig {
    @Bean
    public ShaPasswordEncoder passwordEncoder() {
        return new ShaPasswordEncoder();
    }

    @Bean
    public StringKeyGenerator randomStringGenerator() {
        return org.springframework.security.crypto.keygen.KeyGenerators.string();
    }
}
