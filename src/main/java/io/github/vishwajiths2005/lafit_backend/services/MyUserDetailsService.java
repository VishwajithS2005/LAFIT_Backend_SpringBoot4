package io.github.vishwajiths2005.lafit_backend.services;

import io.github.vishwajiths2005.lafit_backend.models.MyUserDetails;
import io.github.vishwajiths2005.lafit_backend.models.Users;
import io.github.vishwajiths2005.lafit_backend.repositories.UserRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public MyUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @NonNull
    public UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {
        Users user = userRepository.findByUsername(username);

        if(user == null) {
            System.out.println("Username not found.");
            throw new UsernameNotFoundException("Username not found.");
        }

        return new MyUserDetails(user);
    }
}
