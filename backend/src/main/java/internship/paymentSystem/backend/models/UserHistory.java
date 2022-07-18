package com.example.backend.models;

import com.example.backend.models.bases.BaseEntity;
import com.example.backend.models.enumerations.StatusEnum;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "user_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class UserHistory extends BaseEntity { // extends User{
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;

    @Column(name = "username")
    private String username;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

//    @Column(name = "status")
//    @Enumerated(EnumType.STRING)
//    private StatusEnum status;
//
//    @Column(name = "next_status")
//    @Enumerated(EnumType.STRING)
//    private StatusEnum nextStatus;

    @Column(name = "user_id")
    private Long userID;

    @Column(name = "timestamp")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @CreationTimestamp
    private Date timestamp;
    //java util date -> Date

//    public UserHistory(String username, String email, String password, StatusEnum status, StatusEnum nextStatus, Long userID) {
//        super(username,email,password,status,nextStatus);
//        this.userID = userID;
//    }
//
//    public UserHistory(String email) {
//        super(email);
//    }

    public UserHistory(String username, String email, String password, StatusEnum status, StatusEnum nextStatus, Long userID) {
        super(status, nextStatus);
        this.username = username;
        this.email = email;
        this.password = password;
        this.userID = userID;
    }

    public UserHistory(String email) {
        this.email = email;
    }
}
