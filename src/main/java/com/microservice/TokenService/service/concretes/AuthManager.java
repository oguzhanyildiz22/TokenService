package com.microservice.TokenService.service.concretes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
	
	@Override
	public AuthResponseDto login(LoginDto loginDto) {
		
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginDto.getUsername()
						,loginDto.getPassword()));
		SecurityContextHolder.getContext().setAuthentication(authentication);
		String token = jwtGenerator.generateToken(authentication);
		
		AuthResponseDto authResponseDto = new AuthResponseDto();
		authResponseDto.setAccessToken(token);
		return authResponseDto;
	}

	
	@Override
	public String register(RequestDto requestDto){
		if (existsByUsername(requestDto)) {
			
			return "Username is taken!";
		}
		UserEntity user = new UserEntity();
		user.setUsername(requestDto.getUsername());
		user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
		
	
		if (existsByName(requestDto)) {
			return "Wrong role_name. You can only use these words:'ADMIN','OPERATOR','TEAM_LEADER'";
		}
		Role roles = roleRepository.findByName(requestDto.getRole()).get();
		user.setRoles(Collections.singletonList(roles));
		
		userRepository.save(user);
		return "register successful";
		
	}

	@Override
	public boolean existsByUsername(RequestDto requestDto) {
		if (userRepository.existsByUsername(requestDto.getUsername())) {
			return true;
		}
		return false;
	}

	@Override
	public boolean existsByName(RequestDto requestDto) {
		if (!roleRepository.existsByName(requestDto.getRole())) {
			return true;
		}
		return false;
	}


	@Override
	public List<SimpleGrantedAuthority> getRole(String authorizationHeader) {
        String jwtToken = authorizationHeader.substring(7);
		
		if (jwtGenerator.validateToken(jwtToken)) {
			List<SimpleGrantedAuthority> roles = jwtGenerator.getAuthoritiesFromToken(jwtToken);
			
			return roles;
		}
		
		List<SimpleGrantedAuthority> hata = new ArrayList<>();
		return hata;
	}

}
