package com.example.backend.models;

import com.example.backend.models.bases.PersonEntity;
import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "personal_information")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Person extends PersonEntity {

//    @Column(name = "first_name")
//    private String firstName;
//
//    @Column(name = "last_name")
//    private String lastName;
//
//    @Column(name = "address")
//    private String address;
//
//    @Column(name = "date_of_birth")
//    @DateTimeFormat(pattern = "yyyy-MM-dd")
//    private LocalDate dateOfBirth;
//
//    @Column(name = "phone_number")
//    private String phoneNumber;
//
//    @Column(name = "user_id")
//    private Long userID;

}
