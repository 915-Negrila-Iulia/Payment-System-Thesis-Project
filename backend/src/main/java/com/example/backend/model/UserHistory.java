package com.example.backend.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username")
    private String username;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private StatusEnum status;

    @Column(name = "next_status")
    @Enumerated(EnumType.STRING)
    private StatusEnum nextStatus;

    @Column(name = "user_id")
    private Long userID;

    @Column(name = "timestamp")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @CreationTimestamp
    private LocalDateTime timestamp;

    public UserHistory(String username, String email, String password, StatusEnum status, StatusEnum nextStatus, Long userID) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.status = status;
        this.nextStatus = nextStatus;
        this.userID = userID;
    }

    public UserHistory(String email) {
        this.email = email;
    }
}
