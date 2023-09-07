package com.example.demojwtauth.controllers;

import com.example.demojwtauth.entity.Role;
import com.example.demojwtauth.entity.RoleName;
import com.example.demojwtauth.entity.User;
import com.example.demojwtauth.entity.UserProfile;
import com.example.demojwtauth.payload.request.LoginRequest;
import com.example.demojwtauth.payload.request.SignUpRequest;
import com.example.demojwtauth.payload.response.JwtResponse;
import com.example.demojwtauth.repository.RoleRepository;
import com.example.demojwtauth.repository.UserProfileRepository;
import com.example.demojwtauth.repository.UserRepository;
import com.example.demojwtauth.security.jwt.JwtProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.Set;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    UserRepository userRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    PasswordEncoder encoder;
    @Autowired
    JwtProvider jwtProvider;
    @Autowired
    private UserProfileRepository userProfileRepository;
    @PostMapping("/signin")
    public ResponseEntity authUser(@Valid @RequestBody LoginRequest loginRequest){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtProvider.generateJwtToken(authentication);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return ResponseEntity.ok(new JwtResponse(jwt,userDetails.getUsername(), userDetails.getAuthorities()));
    }
    @PostMapping("/signup")
    public ResponseEntity registerUser(@Valid @RequestBody SignUpRequest signUpRequest){
        if(userRepository.existsByUsername(signUpRequest.getUsername())){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if(userRepository.existsByEmail(signUpRequest.getEmail())){
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        User user = new User(signUpRequest.getName(), signUpRequest.getUsername(),
                signUpRequest.getEmail(), encoder.encode(signUpRequest.getPassword()));
        Set<String> strRole = signUpRequest.getRole();
        Set<Role> roles =new HashSet<>();
        strRole.forEach(role -> {
            switch (role) {
                case "admin":
                    Role admin = roleRepository.findByName(RoleName.ROLE_ADMIN)
                            .orElseThrow(()-> new RuntimeException("Role not found"));
                    roles.add(admin);
                    break;
                case "mod":
                    Role mod = roleRepository.findByName(RoleName.ROLE_MODERATOR)
                            .orElseThrow(()-> new RuntimeException("Role not found"));
                    roles.add(mod);
                    break;
                default:
                    Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                            .orElseThrow(()-> new RuntimeException("Role not found"));
                    roles.add(userRole);
            }
        });
        user.setRoles(roles);
        User createUser = userRepository.save(user);
        UserProfile userProfile = new UserProfile();
        userProfile.setUser(createUser);
        userProfileRepository.save(userProfile);
        return ResponseEntity.ok().body("User registered successfully");
    }
}
