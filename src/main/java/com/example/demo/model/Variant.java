package com.example.demo.model;

import com.example.demo.model.Image;
import com.example.demo.model.OptionProduct;
import com.example.demo.model.Product;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Variant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "image_id")
    private Image image;

    private Integer quantity;

    private Double price;

    @ManyToMany(fetch = FetchType.LAZY)
    private Set<OptionProduct> options;

    private Integer stock;

    @Override
    public String toString() {
        return "Variant{" +
                "id=" + id +
                ", product=" + product +
                ", image=" + image +
                ", quantity=" + quantity +
                ", price=" + price +
                ", options=" + options +
                '}';
    }
}