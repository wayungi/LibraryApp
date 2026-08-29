package com.servicecops.project.models.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
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
    private String category;
    private String publisher;
    private Integer publicationYear;
    private String authorsName;
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE},orphanRemoval = true)
    @JoinTable(
            name = "book_information_copies",
            joinColumns = @JoinColumn(
                    name = "book_information_id",
                    nullable = false
            ),
            inverseJoinColumns = @JoinColumn(
                    name = "book_id",
                    nullable = false,
                    unique = true
            )
    )
    @Builder.Default
    private Set<Book> books = new HashSet<>();
}