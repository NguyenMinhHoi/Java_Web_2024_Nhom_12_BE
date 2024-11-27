package com.example.demo.model;

import com.example.demo.utils.enumeration.FormStatus;
import com.example.demo.utils.enumeration.FormType;
import com.example.demo.utils.enumeration.RoyalUser;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
public class MerchantForm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Merchant merchant;

    @Enumerated(EnumType.STRING)
    private FormType formType;

    @Enumerated(EnumType.STRING)
    private FormStatus status;

    @Enumerated(EnumType.STRING)
    private RoyalUser currentStatus;

    @Enumerated(EnumType.STRING)
    private RoyalUser requestedStatus;

    private Instant submissionDate;

    @Lob
    private byte[] documents;

    private Date createddDate;

}