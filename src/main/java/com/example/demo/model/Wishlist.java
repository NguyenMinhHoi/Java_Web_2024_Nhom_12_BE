package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Entity
@Data
@NoArgsConstructor
public class Wishlist {
      @Id
      @GeneratedValue(strategy = GenerationType.IDENTITY)
      private Long id;

      @OneToOne
      private User user;

      @OneToMany
      private Set<Product> products;

      @OneToMany
      private Set<Merchant> shops;
}
