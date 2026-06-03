package io.github.vishwajiths2005.lafit_backend.repositories;

import io.github.vishwajiths2005.lafit_backend.models.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<Users, UUID> {

    Users findByUsername(String username);

    Users findByEmail(String email);

}
