package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "email_configurations")
@Data
public class EmailConfiguration {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String host;
    private int port;
    private String username;
    private String password;
    private String protocol;
    private boolean auth;
    private boolean starttlsEnable;
    private boolean debug;

    // Getters and setters
    // ...
}