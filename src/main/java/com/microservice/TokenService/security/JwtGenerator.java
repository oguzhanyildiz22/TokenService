package com.microservice.TokenService.security;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtGenerator {

	public String generateToken(Authentication authentication) {
		String username = authentication.getName();
		 Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
	        List<String> roles = authorities.stream()
	                .map(GrantedAuthority::getAuthority)
	                .collect(Collectors.toList());
		Date currentDate = new Date();
		Date expireDate = new Date(currentDate.getTime() + SecurityConstants.JWT_EXPIRATION);
		
		
		
		String token = Jwts.builder()
				.setSubject(username)
				.claim("roles", roles)
				.setIssuedAt(new Date())
				.setExpiration(expireDate)
				.signWith(SignatureAlgorithm.HS256, SecurityConstants.JWT_SECRET)
				.compact();
		
		return token;
	}
	
	public String getUsernameFromJWT(String token) {

		return parseJWT(token).getSubject();
	}
	
	public boolean validateToken(String token) {
		try {
			Jwts.parser().setSigningKey(SecurityConstants.JWT_SECRET).parseClaimsJws(token);
			return true;
		} catch (Exception ex) {
			throw new AuthenticationCredentialsNotFoundException("JWT was expired or incorrect");
		}
	}
	public Claims parseJWT(String token) {
		Claims claims = Jwts.parser()
				.setSigningKey(SecurityConstants.JWT_SECRET)
				.parseClaimsJws(token)
				.getBody();
		return claims;
	}

	 public List<SimpleGrantedAuthority> getAuthoritiesFromToken(String token) {
	        Claims claims = Jwts.parser().setSigningKey(SecurityConstants.JWT_SECRET).parseClaimsJws(token).getBody();
	        List<String> roles = claims.get("roles", List.class);
	        return roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
	    }
}
