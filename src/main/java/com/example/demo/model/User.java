package com.example.demo.model;

import com.example.demo.utils.enumeration.RoyalUser;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.antlr.v4.runtime.misc.NotNull;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "userTable")
@Data
@NoArgsConstructor
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String confirmPassword;

    private boolean enabled = true;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_role",
            joinColumns = {@JoinColumn(name = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "role_id")})
    private Set<Role> roles;

    @OneToMany
    private Set<Address> addresses;

    @OneToOne
    private Image avatar;

    private String email;

    private String phoneNumber;

    private String name;

    private String gender;

    private Date birthDate;

    private Date last_time_access;

    private RoyalUser royalUser;

}