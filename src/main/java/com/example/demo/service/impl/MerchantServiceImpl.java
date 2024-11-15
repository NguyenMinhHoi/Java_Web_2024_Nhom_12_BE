package com.example.demo.service.impl;

import com.example.demo.model.*;
import com.example.demo.repository.MerchantRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.service.*;
import com.example.demo.service.dto.MerchantDTO;
import com.example.demo.utils.CommonUtils;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;


import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MerchantServiceImpl implements MerchantService {

    @Autowired
    private MerchantRepository merchantRepository;

    @Autowired
    @Lazy
    private ProductService productService;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private EmailService emailService;
    @Qualifier("userService")
    @Autowired
    private UserService userService;
    @Autowired
    private RoleServiceImpl roleServiceImpl;
    @Autowired
    private ProductRepository productRepository;
    // Inject the OrderServiceImpl

    @Override
    public List<Merchant> findAll() {
        return List.of();
    }

    @Override
    public Merchant findById(Long id) {
        return null;
    }

    @Override
    public void deleteById(Long id) {

    }

    @Override
    public Merchant save(Merchant entity) {

        return null;
    }

    @Override
    public void approveMerchantRegistration(Merchant merchant) {
        // Logic to approve the merchant
        // ...

        // Send acceptance email
        try {
            emailService.sendMerchantRegistrationAcceptance(merchant.getEmail(), merchant.getName());
        } catch (Exception e) {
            System.out.println("Error sending email: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public Merchant createMerchant(Merchant merchant) {
        // Add any necessary validation or business logic here
        Optional<User> user = userService.findById(merchant.getUser().getId());
        if(user.isPresent()){
            merchant.setUser(user.get());
            Role role = roleServiceImpl.findByName("ROLE_MERCHANT");
            user.get().getRoles().add(role);
            userService.save(user.get());
            merchantRepository.save(merchant);
        }else{
            throw new RuntimeException("User not found");
        }
        return merchantRepository.save(merchant);
    }

    @Override
    public Merchant getMerchantById(Long userId) {
        Merchant merchant = merchantRepository.findByUserId(userId);
        if(!CommonUtils.isEmpty(merchant)){
            merchant.setUser(null);
        }
        return merchant;
    }

    public void upgradeToRoyalMerchant(Merchant merchant) {
        try {
            emailService.sendRoyalMerchantRegistrationAcceptance(merchant.getEmail(), merchant.getName());
        } catch (MessagingException e) {
            // Handle the exception (e.g., log it)
            e.printStackTrace();
        }
    }

    @Override
    public MerchantDTO getMerchantByMerchantID(Long id) {
        Optional<Merchant> merchantOptional = merchantRepository.findById(id);
        if(merchantOptional.isPresent()) {
            Merchant merchant = merchantOptional.get();
            List<Product> products = productService.getAllProductByShopId(merchant.getId());

            Set<Review> reviews = new HashSet<>();

            products.stream().forEach(product ->{
                reviews.addAll(reviewService.findByProductId(product.getId()));
            });

            MerchantDTO merchantDTO = MerchantDTO
                    .builder()
                    .id(merchant.getId())
                    .description(merchant.getDescription())
                    .variants(products.stream().map(product -> productService.toProductDTO(product)).collect(Collectors.toSet()))
                    .name(merchant.getName())
                    .sold(CommonUtils.isEmpty(merchant.getTotalSold()) ? 0 : merchant.getTotalSold().longValue())
                    .rating(merchant.getRating())
                    .comments(reviews)
                    .email(merchant.getEmail())
                    .phoneNumber(merchant.getPhoneNumber())
                    .address(merchant.getAddress())
                    .logo(merchant.getLogo() != null ? merchant.getLogo().getPath() : null)
                    .background(merchant.getBackground() != null ? merchant.getBackground().getPath() : null)
                    .last_access(merchant.getLastAccess().toInstant())
                    .build();
            merchantDTO.getComments().stream().forEach(review -> review.getUser().setAddresses(null));
            return merchantDTO;
        }
        return null;
    }
}
