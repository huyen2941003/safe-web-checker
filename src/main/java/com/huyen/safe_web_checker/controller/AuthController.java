package com.huyen.safe_web_checker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.huyen.safe_web_checker.model.request.LoginRequest;
import com.huyen.safe_web_checker.model.request.RegisterRequest;
import com.huyen.safe_web_checker.model.response.JwtAuthenticationResponse;
import com.huyen.safe_web_checker.model.response.RegisterResponse;
import com.huyen.safe_web_checker.security.CustomUserDetails;
import com.huyen.safe_web_checker.security.JwtTokenProvider;
import com.huyen.safe_web_checker.service.AuthService;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String jwt = jwtTokenProvider.generateToken(userDetails);

        return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerForGuest(@RequestBody RegisterRequest registerRequest) {
        try {
            registerRequest.setRoleName("ROLE_USER");
            RegisterResponse response = authService.registerUser(registerRequest);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping(value = "/admin/register")
    public ResponseEntity<?> registerForAdmin(@RequestBody RegisterRequest registerRequest) {
        try {
            registerRequest.setRoleName("ROLE_ADMIN");
            RegisterResponse response = authService.registerUser(registerRequest);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
