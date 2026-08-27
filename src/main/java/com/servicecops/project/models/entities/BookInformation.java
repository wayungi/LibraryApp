package com.servicecops.project.models.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

/**
 * i will use its repo  for recording or saving books.
 * */
@Entity
@Table(name = "book_information")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookInformation extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String name;
    private String publisher;
    private Integer publicationYear;
    private String authorsName;
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private Set<Book> books;
}