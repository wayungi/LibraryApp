package com.servicecops.project.repositories;

import com.servicecops.project.models.entities.Student;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentRepo extends GenericRepository<Student> {
    Optional<Student> findByAdmissionNumber(String admissionNumber);
}
