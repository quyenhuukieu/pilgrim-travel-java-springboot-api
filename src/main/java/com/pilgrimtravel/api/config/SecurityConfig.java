package com.pilgrimtravel.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                //.csrf(csrf -> csrf.disable())
                .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**"))
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/health").permitAll()
                        .requestMatchers("/h2-console/**").permitAll() // Open H2 console routes
                        .requestMatchers("/api/**").authenticated()
                        .anyRequest().permitAll()
                )
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin())) // Allow UI frame rendering
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}
