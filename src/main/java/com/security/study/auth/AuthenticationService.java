package com.security.study.auth;

import com.security.study.config.JwtService;
import com.security.study.user.Role;
import com.security.study.user.User;
import com.security.study.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

   public AuthenticationResponse register(RegisterRequest request) {
       var user = User.builder().
               firstName(request.getFirstName())
               .lastName(request.getLastName())
               .email(request.getEmail())
               .password(passwordEncoder.encode(request.getPassword()))
               .role(Role.USER)
               .build();

       userRepository.save(user);

       var jwtToken = jwtService.generateToken(user);

       return  AuthenticationResponse.builder().token(jwtToken).build();
   }

   public AuthenticationResponse authenticate(AuthenticationRequest request) {

      authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
              request.getEmail(),
              request.getPassword()

      ));
      var user = userRepository.findByEmail(request.getEmail()).orElseThrow();
      var jwtToken = jwtService.generateToken(user);

      return AuthenticationResponse.builder().token(jwtToken).build();
   }
}

