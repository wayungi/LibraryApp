package com.servicecops.project.utils;

import com.servicecops.project.authorities.Permission;
import com.servicecops.project.authorities.Role;
import com.servicecops.project.models.database.SystemPermissionModel;
import com.servicecops.project.models.database.SystemRoleModel;
import com.servicecops.project.models.database.SystemUserModel;
import com.servicecops.project.repositories.SystemRoleRepository;
import com.servicecops.project.repositories.SystemUserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class SetUp {

    private final SystemRoleRepository systemRoleRepository;
    private final SystemUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    /**
     * Creates/updates the default roles and their permissions.
     */
    @PostConstruct
    protected void setupRoles() {

        log.info("Setting up system roles and permissions...");

        List<SystemRoleModel> roles = new ArrayList<>();

        for (Role role : Role.values()) {
            // Check whether the role already exists
            SystemRoleModel roleModel = systemRoleRepository
                    .findFirstByRoleCode(role.name())
                    .orElse(null);

            // Role already exists
            if (roleModel != null) {
                log.debug("Role {} already exists. Skipping.", role.name());
                continue;
            }

            Set<SystemPermissionModel> permissions = role.getPermissions()
                    .stream()
                    .map(permission ->
                            SystemPermissionModel.builder()
                                    .permissionCode(permission.name())
                                    .description(permission.getDescription())
                                    .build()
                    )
                    .collect(Collectors.toSet());

             roleModel = SystemRoleModel.builder()
                    .roleCode(role.name())
                    .description(role.getDescription())
                    .permissions(permissions)
                    .build();

            roles.add(roleModel);
        }

        systemRoleRepository.saveAll(roles);

        log.info("System roles and permissions created successfully.");
    }


    /**
     * Creates the default administrator account.
     */
    @PostConstruct
    protected void createDefaultUser() {

        log.info("Creating default administrator...");

        SystemRoleModel adminRole = systemRoleRepository
                .findFirstByRoleCode(Role.ADMIN.name())
                .orElseThrow(() ->
                        new RuntimeException("ADMIN role was not found")
                );

        Optional<SystemUserModel> existingUser =
                userRepository.findByUsername("admin@gmail.com");

        SystemUserModel systemUser = existingUser.orElseGet(() ->
                SystemUserModel.builder()
                        .isActive(true)
                        .username("admin@gmail.com")
                        .password(passwordEncoder.encode("admin@123"))
                        .lastLoggedInAt(new Date())
                        .role(adminRole)
                        .build()
        );

        userRepository.save(systemUser);

        log.info("Default administrator created successfully.");
    }
}