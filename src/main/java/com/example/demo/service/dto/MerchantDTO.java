package com.example.demo.service.dto;

import com.example.demo.model.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;


import java.time.Instant;
import java.util.Date;
import java.util.Set;

@Builder
@AllArgsConstructor
@Data
public class MerchantDTO {
      private Long id;
      private String name;
      private Image image;
      private String description;
      private Double rating;
      private Long sold;
      private Set<ProductDTO> variants;
      private Set<Review> comments;
      private String phoneNumber;
      private String email;
      private Address address;
      private String logo;
      private String background;
      private Instant last_access;
}
