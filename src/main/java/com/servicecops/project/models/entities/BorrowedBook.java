package com.servicecops.project.models.entities;

import com.servicecops.project.models.database.SystemUserModel;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "borrowed_books")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BorrowedBook extends BaseEntity{

    /**
     * The member who borrowed the book.
     */
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private SystemUserModel user;

    /**
     * The exact physical copy that was borrowed.
     */
    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    /**
     * Date when the member borrowed the book.
     */
    @Column(nullable = false)
    private LocalDate borrowedDate;

    /**
     * Expected return date.
     */
    @Column(nullable = false)
    private LocalDate dueDate;

    /**
     * Actual date when the book was returned.
     * Null means the book has not yet been returned.
     */
    private LocalDate returnedDate;
}