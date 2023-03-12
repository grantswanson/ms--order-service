package com.swansong.orderservice;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
@SpringBootApplication
@EnableFeignClients
public class OrderServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrderServiceApplication.class, args);
	}

	@Bean
	public RequestInterceptor requestTokenBearerInterceptor() {
		return new RequestInterceptor() {
			@Override
			public void apply(RequestTemplate requestTemplate) {
				JwtAuthenticationToken token = (JwtAuthenticationToken) SecurityContextHolder.getContext()
						.getAuthentication();
				if(token == null) {
					System.out.println("RequestInterceptor NO token found. Must have been called directly (not thru gateway)");
				} else {
//					System.out.println("RequestInterceptor adding to the header: "+
//							"Bearer "+ token.getToken().getTokenValue());
					requestTemplate.header("Authorization", "Bearer "+ token.getToken().getTokenValue());
				}
			}
		};
	}

}
