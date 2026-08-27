package com.servicecops.project.repositories;

import com.servicecops.project.models.entities.BookInformation;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepo extends GenericRepository<BookInformation> {
}
