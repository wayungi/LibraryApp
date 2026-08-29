package com.servicecops.project.models.entities;

import com.servicecops.project.models.database.SystemUserModel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Generated;
import java.time.LocalDate;


/**
 * The Student entity represents a student registered. It contains
 * the student's contact information
 * */

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@Table(
        name = "students",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_student_email",
                        columnNames = "email"
                ),
                @UniqueConstraint(
                        name = "uk_student_phone_number",
                        columnNames = "phone_number"
                )
        }
)
public class Student extends BaseEntity{

    @Column(
            name = "admission_number",
            nullable = false,
            unique = true,
            updatable = false
    )
    @Generated
    private String admissionNumber;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Column(nullable = false, length = 20)
    private String gender;

    @Column(length = 100,  nullable = false)
    private String nationality;

    @Column(length = 13, unique = true)
    private String phoneNumber;

    @Column(length = 100, unique = true)
    private String email;

    @Column(name = "country", nullable = false)
    private String country;
// ignore, it's system created.
    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(
            name = "user_id"
    )
    private SystemUserModel user;
}