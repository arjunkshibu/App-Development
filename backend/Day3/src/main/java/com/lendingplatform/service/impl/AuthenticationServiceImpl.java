package com.lendingplatform.service.impl;

import java.util.HashMap;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.lendingplatform.dto.JwtAuthenticationResponse;
import com.lendingplatform.dto.RefreshTokenRequest;
import com.lendingplatform.dto.SigninRequest;
import com.lendingplatform.dto.SignupRequest;
import com.lendingplatform.entities.Role;
import com.lendingplatform.entities.User;
import com.lendingplatform.repo.UserRepository;
import com.lendingplatform.service.AuthenticationService;
import com.lendingplatform.service.JWTService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService{
private final UserRepository userRepository;
private final PasswordEncoder passwordEncoder;
private final JWTService jwtService;
private final AuthenticationManager authenticationManager;
public User signup(SignupRequest signupRequest) {
	User user=new User();
	user.setEmail(signupRequest.getEmail());
	user.setName(signupRequest.getName());
	user.setRole(Role.USER);
	user.setMobilenumber(signupRequest.getPhone());
	user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
	
	return userRepository.save(user);
	
}

public JwtAuthenticationResponse signin(SigninRequest signinRequest) {
	authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(signinRequest.getEmail(),
			signinRequest.getPassword()));
	
	var user=userRepository.findByEmail(signinRequest.getEmail()).orElseThrow(() -> new IllegalArgumentException("Invaild email or password"));
	var jwt=jwtService.generateToken(user);
	var refreshToken=jwtService.generateRefreshToken(new HashMap<>(),user);
	
	JwtAuthenticationResponse jwtAuthenticationResponse=new JwtAuthenticationResponse();
	jwtAuthenticationResponse.setToken(jwt);
	jwtAuthenticationResponse.setRefreshToken(refreshToken);
	return jwtAuthenticationResponse;

}

public JwtAuthenticationResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
	String userEmail=jwtService.extractUserName(refreshTokenRequest.getToken());
	User user=userRepository.findByEmail(userEmail).orElseThrow();
	if(jwtService.isTokenValid(refreshTokenRequest.getToken(), user)) {
		var jwt=jwtService.generateToken(user);
		JwtAuthenticationResponse jwtAuthenticationResponse=new JwtAuthenticationResponse();
		jwtAuthenticationResponse.setToken(jwt);
		jwtAuthenticationResponse.setRefreshToken(refreshTokenRequest.getToken());
		return jwtAuthenticationResponse;
	}
	return null;
}
}
