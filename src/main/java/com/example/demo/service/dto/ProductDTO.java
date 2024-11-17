package com.example.demo.service.dto;

import com.example.demo.model.Category;
import com.example.demo.model.GroupOption;
import com.example.demo.model.Image;
import com.example.demo.model.Merchant;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.Set;

@Builder
@AllArgsConstructor
@Setter
@Getter
@NoArgsConstructor
public class ProductDTO {

    private Long id;

    private String name;

    private String description;

    private Long sold;

    private Boolean isDiscount;

    private Set<Image> image;

    private Double rating;

    private Set<GroupOption> groupOptions;

    private Category category;

    private Double maxPrice;

    private Double minPrice;

    private String categoryName;

    private Date createdAt;


}
