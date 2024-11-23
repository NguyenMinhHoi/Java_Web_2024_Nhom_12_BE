package com.example.demo.service.dto;

import com.example.demo.model.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class FilterDTO {
      private String address;
      private Double priceMax;
      private Double priceMin;
      private Boolean isSale;
      private Category category;
      private Double rating;
}
