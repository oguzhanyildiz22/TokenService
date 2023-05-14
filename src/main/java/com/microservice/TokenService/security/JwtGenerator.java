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

	/**
	 * Generates a JWT token for the given authentication object.
	 * @param authentication The authentication object to generate the token for.
	 * @return A JWT token as a String.
	 */
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
	
	/**
	 * Extracts the username from the given JWT token.
	 * @param token The JWT token from which to extract the username.
	 * @return A string representing the username extracted from the token.
	 * @throws JwtException if the token is invalid or cannot be parsed.
	 */
	public String getUsernameFromJWT(String token) {

		return parseJWT(token).getSubject();
	}
	
	/**
	 * Validates the given JWT token.
	 * @param token The JWT token to be validated.
	 * @return true if the token is valid, false otherwise.
	 * @throws AuthenticationCredentialsNotFoundException if the token is expired or incorrect.
	 */
	public boolean validateToken(String token) {
		try {
			Jwts.parser().setSigningKey(SecurityConstants.JWT_SECRET).parseClaimsJws(token);
			return true;
		} catch (Exception ex) {
			throw new AuthenticationCredentialsNotFoundException("JWT was expired or incorrect");
		}
	}
	
	/**
	 * Parses the given JWT token and returns its claims.
	 * @param token The JWT token to be parsed.
	 * @return A Claims object containing the parsed claims.
	 * @throws JwtException if the token is invalid or cannot be parsed.
	 */
	public Claims parseJWT(String token) {
		Claims claims = Jwts.parser()
				.setSigningKey(SecurityConstants.JWT_SECRET)
				.parseClaimsJws(token)
				.getBody();
		return claims;
	}

	/**
	 * Extracts the list of authorities from the given JWT token.
	 * @param token The JWT token from which to extract the list of authorities.
	 * @return A list of SimpleGrantedAuthority objects representing the authorities extracted from the token.
	 * @throws JwtException if the token is invalid or cannot be parsed.
	 */
	 public List<SimpleGrantedAuthority> getAuthoritiesFromToken(String token) {
	        Claims claims = Jwts.parser().setSigningKey(SecurityConstants.JWT_SECRET).parseClaimsJws(token).getBody();
	        List<String> roles = claims.get("roles", List.class);
	        return roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
	    }
}
