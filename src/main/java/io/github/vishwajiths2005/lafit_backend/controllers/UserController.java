package io.github.vishwajiths2005.lafit_backend.controllers;

import io.github.vishwajiths2005.lafit_backend.dtos.ChangeRoleRequest;
import io.github.vishwajiths2005.lafit_backend.dtos.UserLoginResponse;
import io.github.vishwajiths2005.lafit_backend.dtos.UserResponse;
import io.github.vishwajiths2005.lafit_backend.dtos.UserUpdateRequest;
import io.github.vishwajiths2005.lafit_backend.enums.Role;
import io.github.vishwajiths2005.lafit_backend.models.*;
import io.github.vishwajiths2005.lafit_backend.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    //Register a new user into the db.
    @PostMapping
    public UserResponse register(@Valid @RequestBody Users user) {
        user.setRole(Role.USER);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userService.register(user);
    }

    //Delete a user from the db.
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        userService.delete(id);
    }

    @DeleteMapping("")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSelf(@AuthenticationPrincipal MyUserDetails userDetails) {
        userService.delete(userDetails.getId());
    }

    //Get a list of all registered users available in the db.
    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public List<UserResponse> getAllUsers() {
        return userService.getAllUsers();
    }

    //Log into the system and get user details and jwt.
    @PostMapping("/login")
    public UserLoginResponse login(@RequestBody Users user) {
        return userService.login(user);
    }

    //Change username, password or email.
    @PutMapping("/{id}")
    public UserResponse update(
            @PathVariable UUID id,
            @Valid @RequestBody UserUpdateRequest userUpdateRequest
    ) throws Exception {
        return userService.update(id, userUpdateRequest);
    }

    //Change roles for any existing user.
    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public UserResponse changeRole(
            @PathVariable UUID id,
            @RequestBody ChangeRoleRequest changeRoleRequest
    ) throws Exception {
        return userService.changeRole(id, changeRoleRequest.getRole());
    }

}
