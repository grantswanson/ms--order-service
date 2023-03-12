package com.swansong.orderservice;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class  SecurityConfig   {

    /* This enables security in this service and prevents unauthorized access
     * It also grabs the JTW token and makes it later available in the application context
     * JwtAuthenticationToken token = (JwtAuthenticationToken) SecurityContextHolder.getContext()
     * 						.getAuthentication()
     *
     * Originally we were going to extend WebSecurityConfigurerAdapter and override the configure method,
     * but the class is deprecated
     * Instead we register a SecurityFilterChain
     * see: https://spring.io/blog/2022/02/21/spring-security-without-the-websecurityconfigureradapter
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authorize -> authorize.anyRequest().authenticated())
                .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt);
        return http.build();
    }
}
