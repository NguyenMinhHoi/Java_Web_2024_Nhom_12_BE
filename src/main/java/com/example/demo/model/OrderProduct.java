package com.example.demo.model;


import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.relational.core.sql.In;

@Entity
@Data
@NoArgsConstructor
public class OrderProduct {
    @EmbeddedId
    private ProductOrderPK productOrderPK;

    private Integer quantity;

    private Double price;

    private Double getPrices(){
         return this.productOrderPK.getVariant().getPrice() * quantity;
    }

    public Double getPrice(){return this.price;}

    private Product getProduct(){
           return this.productOrderPK.getVariant().getProduct();
    }

}
