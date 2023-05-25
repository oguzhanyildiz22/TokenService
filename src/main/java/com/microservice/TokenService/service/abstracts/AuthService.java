package com.microservice.TokenService.service.abstracts;

import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.RequestHeader;

import com.microservice.TokenService.dto.AuthResponseDto;
import com.microservice.TokenService.dto.LoginDto;
import com.microservice.TokenService.dto.RequestDto;

public interface AuthService {

	/**
	 * Bearer token is returned to the user by asking the registered 
	 * user to enter a username and password.
	 * 
	 * @param  loginDto :<br>  String username <br> String password
	 * @return  String bearer token 
	 */
	AuthResponseDto login(LoginDto loginDto);
	
	/**
	 * user registers with username, password and role
	 * 
	 * @param  requestDto : <br> String username <br> String password <br> String role
	 * @return String message 
	 */
	String register(RequestDto requestDto);
	
	
	/**
	 * Returns the roles of the user with the JWT token in the Authorization header.
	 * 
	 * @param authorizationHeader the Authorization header containing the JWT token
	 * @return a list of SimpleGrantedAuthority objects representing the roles of the user
	 * @throws AuthenticationCredentialsNotFoundException if the JWT token is expired or incorrect
	 */
	List<SimpleGrantedAuthority> getRole(@RequestHeader("Authorization") String authorizationHeader);
}
