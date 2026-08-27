package com.servicecops.project.config;

import com.servicecops.project.models.database.SystemUserModel;
import com.servicecops.project.repositories.SystemUserRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class ApplicationConf implements UserDetailsService {
    private final SystemUserRepository userRepository;

    @NullMarked
    @Override
    public SystemUserModel loadUserByUsername(String username) throws UsernameNotFoundException {

        Optional<SystemUserModel> usersModel = userRepository.findByUsername(username);
        if (usersModel.isPresent()) {
            SystemUserModel user = usersModel.get();
            // if the user account is not activated, then we eject from here.
            if (user.getIsActive() == Boolean.FALSE) {
                throw new IllegalStateException("User account is not active");
            }
            return user;
        } else {
            throw new IllegalStateException("User not found");
        }
    }
}
