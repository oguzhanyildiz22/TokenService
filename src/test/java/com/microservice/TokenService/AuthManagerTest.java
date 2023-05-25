package com.microservice.TokenService;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.microservice.TokenService.dto.AuthResponseDto;
import com.microservice.TokenService.dto.LoginDto;
import com.microservice.TokenService.dto.RequestDto;
import com.microservice.TokenService.entity.UserEntity;
import com.microservice.TokenService.repository.RoleRepository;
import com.microservice.TokenService.repository.UserRepository;
import com.microservice.TokenService.security.JwtGenerator;
import com.microservice.TokenService.service.concretes.AuthManager;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
public class AuthManagerTest {

    @InjectMocks
    private AuthManager authManager;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtGenerator jwtGenerator;
    
    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testLogin() {
        // Arrange
        LoginDto loginDto = new LoginDto();
        loginDto.setUsername("testuser");
        loginDto.setPassword("testpassword");

        String generatedToken = "generated_token";

        Authentication mockedAuthentication = new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword());
        SecurityContextHolder.getContext().setAuthentication(mockedAuthentication);

        Mockito.when(authenticationManager.authenticate(Mockito.any(Authentication.class)))
                .thenReturn(mockedAuthentication);

        Mockito.when(jwtGenerator.generateToken(Mockito.any(Authentication.class)))
                .thenReturn(generatedToken);

        // Act
        AuthResponseDto result = authManager.login(loginDto);

        // Assert
        Assert.assertEquals(generatedToken, result.getAccessToken());
        Mockito.verify(authenticationManager, Mockito.times(1)).authenticate(Mockito.any(Authentication.class));
        Mockito.verify(jwtGenerator, Mockito.times(1)).generateToken(Mockito.any(Authentication.class));
    }
    
    @Test
    public void testRegister() {
    	
    	//Arrange
    	RequestDto requestDto = new RequestDto();
    	requestDto.setUsername("testuser");
    	requestDto.setPassword("testpassword");
    	requestDto.setRole("ADMIN");
    	
    	UserEntity existingUser = new UserEntity();
    	existingUser.setUsername(requestDto.getUsername());
        
    	Mockito.when(userRepository.existsByUsername(requestDto.getUsername())).thenReturn(true);
     // Mockito.when(roleRepository.existsByName(Mockito.eq(requestDto.getRole()))).thenReturn(false);

        // Act
    	String result = authManager.register(requestDto);
    	
    	// Assert
        Assert.assertEquals("Username is taken!", result);
        Mockito.verify(userRepository, Mockito.times(1)).existsByUsername(requestDto.getUsername());
     // Mockito.verify(roleRepository, Mockito.times(1)).existsByName(requestDto.getRole());
        Mockito.verify(userRepository, Mockito.times(0)).save(Mockito.any(UserEntity.class));
    	
    	
    	
    	
    }
    @Test
    public void testGetRole_ValidToken() {
        // Arrange
        String authorizationHeader = "Bearer valid_token";

        List<SimpleGrantedAuthority> expectedRoles = Arrays.asList(
            new SimpleGrantedAuthority("ROLE_ADMIN")
            
        );

        Mockito.when(jwtGenerator.validateToken(Mockito.anyString())).thenReturn(true);
        Mockito.when(jwtGenerator.getAuthoritiesFromToken(Mockito.anyString())).thenReturn(expectedRoles);

        // Act
        List<SimpleGrantedAuthority> result = authManager.getRole(authorizationHeader);

        // Assert
        Assert.assertEquals(expectedRoles, result);
        Mockito.verify(jwtGenerator, Mockito.times(1)).validateToken(Mockito.anyString());
        Mockito.verify(jwtGenerator, Mockito.times(1)).getAuthoritiesFromToken(Mockito.anyString());
    }

    @Test
    public void testGetRole_InvalidToken() {
        // Arrange
        String authorizationHeader = "Bearer invalid_token";

        Mockito.when(jwtGenerator.validateToken(Mockito.anyString())).thenReturn(false);

        // Act
        List<SimpleGrantedAuthority> result = authManager.getRole(authorizationHeader);

        // Assert
        Assert.assertTrue(result.isEmpty());
        Mockito.verify(jwtGenerator, Mockito.times(1)).validateToken(Mockito.anyString());
        Mockito.verify(jwtGenerator, Mockito.times(0)).getAuthoritiesFromToken(Mockito.anyString());
    }
}



