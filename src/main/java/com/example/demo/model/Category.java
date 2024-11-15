package com.example.demo.model;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Category {
      @Id
      @GeneratedValue(strategy = GenerationType.IDENTITY)
      private int id;
      private String name;

      @OneToOne
      private Image image;
}
