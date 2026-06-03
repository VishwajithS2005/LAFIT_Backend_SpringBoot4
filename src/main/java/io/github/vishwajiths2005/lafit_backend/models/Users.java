package io.github.vishwajiths2005.lafit_backend.models;

import io.github.vishwajiths2005.lafit_backend.dtos.UserResponse;
import io.github.vishwajiths2005.lafit_backend.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;
import java.util.UUID;

@Getter
@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Setter
    @Column(name = "username", unique = true, nullable = false)
    private String username;

    @Setter
    @Column(name = "password", nullable = false)
    private String password;

    @Setter
    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Setter
    @Column(name = "email", nullable = false, unique = true)
    @Email
    private String email;

    public UserResponse userResponse() {
        return new UserResponse(this);
    }

}
