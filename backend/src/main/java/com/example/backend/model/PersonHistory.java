package com.example.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "person_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "address")
    private String address;

    @Column(name = "date_of_birth")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "user_id")
    private Long userID;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    @ColumnDefault(value = "APPROVE")
    private StatusEnum status;

    @Column(name = "person_id")
    private Long personID;

    @Column(name = "timestamp")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @CreationTimestamp
    private LocalDateTime timestamp;

    public PersonHistory(String firstName, String lastName, String address,
                         LocalDate dateOfBirth, String phoneNumber, Long userID, StatusEnum status, Long personID) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.dateOfBirth = dateOfBirth;
        this.phoneNumber = phoneNumber;
        this.userID = userID;
        this.status = status;
        this.personID = personID;
    }
}
