package com.example.demo.model;

import com.example.demo.utils.enumeration.OrderStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.util.Set;


@Entity
@NoArgsConstructor
@Data
@Builder
@AllArgsConstructor
public class Orders {

      @Id
      @GeneratedValue(strategy = GenerationType.IDENTITY)
      private Long id;

      private Date date;

      @ManyToOne
      private Voucher voucher;

      @ManyToMany
      private Set<OrderProduct> products;

      @ManyToOne
      private Address address;

      private Double total;

      @ManyToOne
      private Merchant merchant;

      @ManyToOne
      private User user;

      private OrderStatus status;

}
