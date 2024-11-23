package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@JsonIgnoreProperties({"hibernateLazyInitializer"})
@Embeddable
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
@EqualsAndHashCode(exclude = {"order", "variant"})
@ToString(exclude = {"order", "variant"})
public class ProductOrderPK implements Serializable {
    @JsonBackReference
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Orders order;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Variant variant;

}