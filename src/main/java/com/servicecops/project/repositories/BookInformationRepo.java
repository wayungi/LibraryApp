package com.servicecops.project.repositories;

import com.servicecops.project.models.entities.BookInformation;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookInformationRepo extends GenericRepository<BookInformation> {
    Optional<BookInformation> findByCategory(String category);
}
