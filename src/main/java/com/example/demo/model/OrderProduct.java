package com.example.demo.model;


import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.sql.In;

@Entity
@Data
@NoArgsConstructor
public class OrderProduct {
    @EmbeddedId
    private ProductOrderPK productOrderPK;

    private Integer quantity;

    private Double getPrice(){
         return this.productOrderPK.getVariant().getPrice() * quantity;
    }

    private Product getProduct(){
           return this.productOrderPK.getVariant().getProduct();
    }
}
