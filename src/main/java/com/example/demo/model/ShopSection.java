package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;
import java.util.Set;

@Entity
@Data
@Table(name = "shop_sections")
public class ShopSection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToMany
    private Set<Product> productSet;

    @ManyToOne
    private Merchant merchant;

    private Date createdAt;

    private Date updateAt;
}