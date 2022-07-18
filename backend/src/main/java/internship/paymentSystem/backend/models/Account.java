package com.example.backend.models;

import com.example.backend.models.bases.AccountEntity;
import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "account")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Account extends AccountEntity {
}
