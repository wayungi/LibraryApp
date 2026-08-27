package com.servicecops.project.models.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Check;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "books")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Book extends BaseEntity {

    @Column(unique = true)
    private String isbn;
    private String status;// borrowed/available.

    // available books

}