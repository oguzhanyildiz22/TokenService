package com.microservice.TokenService.dto;

import lombok.Data;

@Data
public class RequestDto {

	private String username;
	private String password;
	private String role;
}