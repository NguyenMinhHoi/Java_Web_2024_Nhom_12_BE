package com.example.demo.model;

import com.example.demo.utils.enumeration.OrderStatus;
import com.example.demo.utils.enumeration.PaymentType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import java.time.Instant;
import java.util.Date;
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

      private String address;

      private Double total;

      @ManyToOne
      private Merchant merchant;

      private String name;

      private String phone;

      private String email;

      @ManyToOne
      private User user;

      private PaymentType paymentType;

      private Date lastTimeUpdated;

      private OrderStatus status;

}
