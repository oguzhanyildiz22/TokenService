package com.microservice.TokenService.service.abstracts;

import com.microservice.TokenService.dto.AuthResponseDto;
import com.microservice.TokenService.dto.LoginDto;
import com.microservice.TokenService.dto.RequestDto;

public interface AuthService {

	AuthResponseDto login(LoginDto loginDto);
	
	String register(RequestDto requestDto);
	
	boolean existsByUsername(RequestDto requestDto);
	
	boolean existsByName(RequestDto requestDto);
}
