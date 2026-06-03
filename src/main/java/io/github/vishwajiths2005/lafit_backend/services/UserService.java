package io.github.vishwajiths2005.lafit_backend.services;

import io.github.vishwajiths2005.lafit_backend.dtos.UserLoginResponse;
import io.github.vishwajiths2005.lafit_backend.dtos.UserResponse;
import io.github.vishwajiths2005.lafit_backend.dtos.UserUpdateRequest;
import io.github.vishwajiths2005.lafit_backend.enums.Role;
import io.github.vishwajiths2005.lafit_backend.models.*;
import io.github.vishwajiths2005.lafit_backend.repositories.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final AuthenticationManager authManager;
    private final JWTService jwtService;
    private final PasswordEncoder bCrypt;

    public UserService(UserRepository userRepository, AuthenticationManager authManager, JWTService jwtService, PasswordEncoder bCrypt) {
        this.userRepository = userRepository;
        this.authManager = authManager;
        this.jwtService = jwtService;
        this.bCrypt = bCrypt;
    }

    public UserResponse register(Users user) {
        return new UserResponse(userRepository.save(user));
    }

    public void delete(UUID id) {
        userRepository.deleteById(id);
    }

    public List<UserResponse> getAllUsers() {
        return userRepository
                .findAll()
                .stream()
                .map(Users::userResponse)
                .collect(Collectors.toList());
    }

    public String verify(Users user) {
        Authentication authentication =
                authManager.authenticate(new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));
        if(authentication.isAuthenticated())
            return jwtService.generateToken(user.getUsername());
        return "Failure.";
    }

    public UserLoginResponse login(Users user) {
        return new UserLoginResponse(userRepository.findByUsername(user.getUsername()), verify(user));
    }

    public UserResponse update(UUID id, UserUpdateRequest userUpdateRequest) throws Exception {
        Optional<Users> exists = userRepository.findById(id);
        Users user = null;
        if(exists.isPresent()) {
            user = exists.get();
            if(userUpdateRequest.getNewPassword() != null && userUpdateRequest.getOldPassword() != null) {
                if(bCrypt.matches(userUpdateRequest.getOldPassword(), user.getPassword())) {
                    String newP = bCrypt.encode(userUpdateRequest.getNewPassword());
                    user.setPassword(newP);
                }
                else {
                    throw new Exception("Old Password is not the same.");
                }
            }
            if(userUpdateRequest.getUsername() != null) {
                if(userRepository.findByUsername(userUpdateRequest.getUsername()) == null) {
                    user.setUsername(userUpdateRequest.getUsername());
                }
                else {
                    throw new Exception("Username already present.");
                }
            }
            if(userUpdateRequest.getEmail() != null) {
                if(userRepository.findByEmail(userUpdateRequest.getEmail()) == null) {
                    user.setEmail(userUpdateRequest.getEmail());
                }
                else {
                    throw new Exception("Email already present.");
                }
            }
        }
        if(user == null) {
            return null;
        }
        return new UserResponse(userRepository.save(user));
    }

    public UserResponse changeRole(UUID id, Role role) throws Exception {
        Optional<Users> exists = userRepository.findById(id);
        Users user;
        if(exists.isPresent()) {
            user = exists.get();
            user.setRole(role);
            return new UserResponse(userRepository.save(user));
        }
        else {
            throw new Exception("There is no valid user with this id.");
        }
    }
}
