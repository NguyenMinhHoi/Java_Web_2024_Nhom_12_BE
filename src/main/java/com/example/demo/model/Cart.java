package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Entity
@Data
@NoArgsConstructor
public class Cart {
      @Id
      @GeneratedValue(strategy = GenerationType.IDENTITY)
      private Long id;

      @OneToOne
      private User user;

      @ManyToMany
      private Set<Variant> variants;
}
