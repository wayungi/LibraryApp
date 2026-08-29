package com.servicecops.project.repositories;

import com.servicecops.project.models.entities.Book;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookRepository extends GenericRepository<Book> {
    Optional<Book> findByIsbn(String isbn);
}
