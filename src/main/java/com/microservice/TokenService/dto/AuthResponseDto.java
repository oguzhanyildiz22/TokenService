package com.microservice.TokenService.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponseDto {

	private String accessToken;
	private String tokenType = "Bearer ";
	
	public AuthResponseDto (String accessToken) {
		this.accessToken=accessToken;
	}
}
