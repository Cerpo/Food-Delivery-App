package com.cerpo.fd.service;

import com.cerpo.fd.util.AppUtils;
import com.cerpo.fd.exception.FDApiException;
import com.cerpo.fd.model.user.Role;
import com.cerpo.fd.model.user.User;
import com.cerpo.fd.model.user.UserRepository;
import com.cerpo.fd.payload.auth.SignInRequest;
import com.cerpo.fd.payload.auth.AuthResponse;
import com.cerpo.fd.payload.auth.SignUpRequest;
import com.cerpo.fd.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private static final String TOKEN_TYPE = "Bearer ";

    private final UserRepository        userRepository;
    private final PasswordEncoder       passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider      jwtTokenProvider;

    public AuthResponse register(SignUpRequest request) {
        userRepository.findByEmail(request.getEmail()).ifPresent(user ->
        { throw new FDApiException(HttpStatus.BAD_REQUEST, "Email is already taken"); } );
        var user = new User(request.getEmail(),
                            passwordEncoder.encode(request.getPassword()),
                            AppUtils.getDate(null),
                            Role.ROLE_CUSTOMER);
        userRepository.save(user);
        var jwtToken = jwtTokenProvider.generateToken(user);
        return new AuthResponse(TOKEN_TYPE, jwtToken);
    }

    public AuthResponse authenticate(SignInRequest request) {
        var user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new FDApiException(HttpStatus.BAD_REQUEST, "Bad credentials"));
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        var jwtToken = jwtTokenProvider.generateToken(user);
        updateLoginDate(user);
        return new AuthResponse(TOKEN_TYPE, jwtToken);
    }

    private void updateLoginDate(User user) {
        user.setLastLoginDate(AppUtils.getDate(null));
        userRepository.save(user);
    }
}
