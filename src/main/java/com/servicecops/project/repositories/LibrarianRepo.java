package com.servicecops.project.repositories;

import com.servicecops.project.models.entities.Librarian;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LibrarianRepo extends  GenericRepository<Librarian> {
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);

    Optional<Librarian> findFirstByPhoneNumber(String phoneNumber);

    Optional<Librarian> findFirstByEmail(String email);

    Optional<Librarian> findByEmployeeNumber(String employeeNumber);
}
