package com.huyen.safe_web_checker.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.huyen.safe_web_checker.domain.Role;
import com.huyen.safe_web_checker.domain.User;
import com.huyen.safe_web_checker.model.request.RegisterRequest;
import com.huyen.safe_web_checker.model.response.RegisterResponse;
import com.huyen.safe_web_checker.repository.RoleRepository;
import com.huyen.safe_web_checker.repository.UserRepository;
import com.huyen.safe_web_checker.service.AuthService;

import jakarta.transaction.Transactional;

@Service
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthServiceImpl(UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public RegisterResponse registerUser(RegisterRequest registerRequest) {

        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new RuntimeException("Username đã tồn tại!");
        }

        Role role = roleRepository.findByRoleName(registerRequest.getRoleName())
                .orElseThrow(() -> new RuntimeException(
                        "Không có role nào tên: " + registerRequest.getRoleName()));

        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setEmail(registerRequest.getEmail());
        user.setRoleName(role);

        User savedUser = userRepository.save(user);

        return new RegisterResponse("Đăng ký tài khoản thành công!", savedUser.getUsername(), savedUser.getEmail(),
                savedUser.getRoleName().getRoleName());
    }

}
