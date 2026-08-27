package com.servicecops.project.models.entities;
import com.servicecops.project.models.database.SystemUserModel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Generated;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(
        name = "librarians",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_librarian_employee_number",
                        columnNames = "employee_number"
                )
        }
)
public class Librarian extends BaseEntity {

    @Generated
    @Column(
            name = "employee_number",
            nullable = false,
            unique = true,
            length = 30
    )
    private String employeeNumber;

    @Column(
            name = "name",
            nullable = false,
            length = 50
    )
    private String name;

    @Column(nullable = false, length = 20)
    private String gender;

    @Column(length = 100, unique = true)
    private String email;

    @Column(length = 30, unique = true)
    private String phoneNumber;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "date_joined")
    private LocalDate dateJoined;

    //ignore this field
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(
            name = "user_id"
    )
    private SystemUserModel user;
}