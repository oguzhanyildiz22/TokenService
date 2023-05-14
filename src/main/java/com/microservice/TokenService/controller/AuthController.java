package com.microservice.TokenService.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.microservice.TokenService.dto.AuthResponseDto;
import com.microservice.TokenService.dto.LoginDto;
import com.microservice.TokenService.dto.RequestDto;
import com.microservice.TokenService.entity.Role;
import com.microservice.TokenService.entity.UserEntity;
import com.microservice.TokenService.repository.RoleRepository;
import com.microservice.TokenService.repository.UserRepository;
import com.microservice.TokenService.security.JwtGenerator;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

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
	


	@PostMapping("login")
	public ResponseEntity<AuthResponseDto> login(@RequestBody LoginDto loginDto){
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginDto.getUsername()
						,loginDto.getPassword()));
		SecurityContextHolder.getContext().setAuthentication(authentication);
		String token = jwtGenerator.generateToken(authentication);
		
		return new ResponseEntity<>(new AuthResponseDto(token),HttpStatus.OK);
	}
	
	@PostMapping("register")
	public ResponseEntity<String> register(@RequestBody RequestDto requestDto){
		if (userRepository.existsByUsername(requestDto.getUsername())) {
			return new ResponseEntity<>("Username is taken!",HttpStatus.BAD_REQUEST); 
		}
		UserEntity user = new UserEntity();
		user.setUsername(requestDto.getUsername());
		user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
		
	
		if (!roleRepository.existsByName(requestDto.getRole())) {
			return new ResponseEntity<>("Wrong role_name. You can only use these words:'ADMIN','OPERATOR','TEAM_LEADER'",HttpStatus.BAD_REQUEST);
		}
		Role roles = roleRepository.findByName(requestDto.getRole()).get();
		user.setRoles(Collections.singletonList(roles));
		
		userRepository.save(user);
		
		return new ResponseEntity<>("User registered success!",HttpStatus.OK);
	}
	
	@GetMapping("/getRole")
	public List<SimpleGrantedAuthority> getRole(@RequestHeader("Authorization") String authorizationHeader){
		String jwtToken = authorizationHeader.substring(7);
		
		if (jwtGenerator.validateToken(jwtToken)) {
			List<SimpleGrantedAuthority> roles = jwtGenerator.getAuthoritiesFromToken(jwtToken);
			
			return roles;
		}
		
		List<SimpleGrantedAuthority> hata = new ArrayList<>();
		return hata;
	}
	
	

	
	
	
}
