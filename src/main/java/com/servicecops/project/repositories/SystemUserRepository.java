package com.servicecops.project.repositories;

import com.servicecops.project.models.database.*;
import com.servicecops.project.models.jpahelpers.repository.JetRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SystemUserRepository extends JetRepository<SystemUserModel, Long> {
    Optional<SystemUserModel> findByUsername(String username);
}

