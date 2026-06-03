package io.github.vishwajiths2005.lafit_backend.dtos;

import io.github.vishwajiths2005.lafit_backend.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChangeRoleRequest {
    private Role role;
}
