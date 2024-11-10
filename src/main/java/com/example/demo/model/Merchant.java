package com.example.demo.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Merchant {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String name;

        @OneToOne(cascade = CascadeType.ALL)
        private Address address;

        private String description;

        private String email;

        @OneToMany
        private Set<Product> products;

        @OneToMany
        private Set<Voucher> vouchers;

        private Boolean isRoyal;

        private Double rating;

        private Double totalSold;

        @OneToOne
        private User user;
}
