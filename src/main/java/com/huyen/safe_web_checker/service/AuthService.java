package com.huyen.safe_web_checker.service;

import org.springframework.stereotype.Service;

import com.huyen.safe_web_checker.model.request.RegisterRequest;
import com.huyen.safe_web_checker.model.response.RegisterResponse;

public interface AuthService {
        RegisterResponse registerUser(RegisterRequest registerRequest);
}