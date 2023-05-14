package com.microservice.TokenService.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.microservice.TokenService.dto.AuthResponseDto;
import com.microservice.TokenService.dto.LoginDto;
import com.microservice.TokenService.dto.RequestDto;
import com.microservice.TokenService.security.JwtGenerator;
import com.microservice.TokenService.service.abstracts.AuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	@Autowired
	private AuthService authService;
	
	@Autowired
	private JwtGenerator jwtGenerator;
	


	@PostMapping("login")
	public ResponseEntity<AuthResponseDto> login(@RequestBody LoginDto loginDto){

		AuthResponseDto authResponseDto = authService.login(loginDto);
		return new ResponseEntity<>(authResponseDto,HttpStatus.OK);
	}
	
	@PostMapping("register")
	public String register(@RequestBody RequestDto requestDto){
		return authService.register(requestDto);
	}
	
	
	@GetMapping("/getRole")
	public List<SimpleGrantedAuthority> getRole(@RequestHeader("Authorization") String authorizationHeader){
//		String jwtToken = authorizationHeader.substring(7);
//		
//		if (jwtGenerator.validateToken(jwtToken)) {
//			List<SimpleGrantedAuthority> roles = jwtGenerator.getAuthoritiesFromToken(jwtToken);
//			
//			return roles;
//		}
//		
//		List<SimpleGrantedAuthority> hata = new ArrayList<>();
//		return hata;
		return authService.getRole(authorizationHeader);
	}
	
	

	
	
	
}
