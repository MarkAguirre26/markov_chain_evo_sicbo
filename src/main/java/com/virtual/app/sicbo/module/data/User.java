package com.virtual.app.sicbo.module.data;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "user")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "uuid", length = 254)
    private String uuid;

    @Column(name = "username", length = 254)
    private String username;

    @Column(name = "email", length = 254)
    private String email;

    @Column(name = "password", length = 254)
    private String password;

    @Column(name = "role", length = 50)
    private String role;

    @Column(name = "is_active", length = 10)
    private int isActive;

    @Column(name = "logged", length = 10)
    private int logged;

    @Column(name = "date_last_modified")
    private LocalDateTime dateLastModified;

    @Column(name = "date_created",  updatable = false, columnDefinition = "datetime default current_timestamp()")
    private LocalDateTime dateCreated;


}
