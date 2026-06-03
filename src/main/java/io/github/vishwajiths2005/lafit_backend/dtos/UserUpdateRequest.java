package io.github.vishwajiths2005.lafit_backend.dtos;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateRequest {

    @Nullable
    private String username;

    @Nullable
    private String oldPassword;

    @Nullable
    private String newPassword;

    @Email
    @Nullable
    private String email;
}
