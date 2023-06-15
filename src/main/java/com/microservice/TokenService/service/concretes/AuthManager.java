package com.microservice.TokenService.service.concretes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.microservice.TokenService.dto.AuthResponseDto;
import com.microservice.TokenService.dto.LoginDto;
import com.microservice.TokenService.dto.RequestDto;
import com.microservice.TokenService.entity.Role;
import com.microservice.TokenService.entity.UserEntity;
import com.microservice.TokenService.repository.RoleRepository;
import com.microservice.TokenService.repository.UserRepository;
import com.microservice.TokenService.security.JwtGenerator;
import com.microservice.TokenService.service.abstracts.AuthService;
@Service
public class AuthManager implements AuthService{

	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private RoleRepository roleRepository;
	@Autowired
	private PasswordEncoder  passwordEncoder;
	@Autowired
	private JwtGenerator jwtGenerator;
	
	private static final Logger logger = LogManager.getLogger(AuthManager.class);
	
	@Override
	public AuthResponseDto login(LoginDto loginDto) {
		
		logger.info("Begin login method to Authenticate user {}", loginDto.getUsername());
		
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginDto.getUsername()
						,loginDto.getPassword()));
		
		logger.info("User {} authenticated successfully", loginDto.getUsername());
		
		SecurityContextHolder.getContext().setAuthentication(authentication);
		String token = jwtGenerator.generateToken(authentication);
		
		logger.info("Generated JWT token for user {}", loginDto.getUsername());
		
		AuthResponseDto authResponseDto = new AuthResponseDto();
		authResponseDto.setAccessToken(token);
		
		logger.info("Returning auth response for user {}", loginDto.getUsername());
		
		return authResponseDto;
	}

	
	@Override
	public String register(RequestDto requestDto){
		
		logger.info("Begin register method for Registering user {}", requestDto.getUsername());
		
		if (userRepository.existsByUsername(requestDto.getUsername())) {
			logger.warn("Username {} is taken", requestDto.getUsername());
			return "Username is taken!";
		}
		UserEntity user = new UserEntity();
		user.setUsername(requestDto.getUsername());
		user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
		
	
		if (!roleRepository.existsByName(requestDto.getRole())) {
			 logger.warn("Role name {} is invalid", requestDto.getRole());
			return "Wrong role_name. You can only use these words:'ADMIN','OPERATOR','TEAM_LEADER'";
		}
		Role roles = roleRepository.findByName(requestDto.getRole()).get();
		user.setRoles(Collections.singletonList(roles));
		
		userRepository.save(user);
		
		logger.info("User {} registered successfully", requestDto.getUsername());
		return "register successful";
		
	}


	@Override
	public List<SimpleGrantedAuthority> getRole(String authorizationHeader) {
		logger.info("getRole() function started.");
        String jwtToken = authorizationHeader.substring(7);
		
		if (jwtGenerator.validateToken(jwtToken)) {
			List<SimpleGrantedAuthority> roles = jwtGenerator.getAuthoritiesFromToken(jwtToken);
			logger.info("getRole() function ended. Returning roles: " + roles.toString());
			return roles;
		}
		logger.info("getRole() function ended. Returning empty list.");
		List<SimpleGrantedAuthority> hata = new ArrayList<>();
		return hata;
	}

}
