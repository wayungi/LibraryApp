package com.servicecops.project.models.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

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