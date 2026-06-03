package io.github.vishwajiths2005.lafit_backend.dtos;

import io.github.vishwajiths2005.lafit_backend.enums.Role;
import io.github.vishwajiths2005.lafit_backend.models.Users;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UserLoginResponse {
    private UUID id;
    private String username;
    private Role role;
    private String email;
    private String token;

    public UserLoginResponse(Users user, String token) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.role = user.getRole();
        this.email = user.getEmail();
        this.token = token;
    }
}
