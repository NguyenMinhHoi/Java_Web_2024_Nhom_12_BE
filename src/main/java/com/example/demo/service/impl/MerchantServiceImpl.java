package com.example.demo.service.impl;

import com.example.demo.model.*;
import com.example.demo.repository.MerchantFormRepository;
import com.example.demo.repository.MerchantRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.service.*;
import com.example.demo.service.dto.MerchantDTO;
import com.example.demo.utils.CommonUtils;
import com.example.demo.utils.enumeration.FormStatus;
import com.example.demo.utils.enumeration.FormType;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;


import java.util.*;
import java.util.stream.Collectors;

@Service
public class MerchantServiceImpl implements MerchantService {

    @Autowired
    private MerchantRepository merchantRepository;

    @Autowired
    private MerchantFormRepository merchantFormRepository;

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
    public void approveMerchantRegistration(MerchantForm merchantForm) {
        Merchant merchant = merchantForm.getMerchant();
        merchantForm.setStatus(FormStatus.APPROVED);
        merchantFormRepository.save(merchantForm);
        try {
            emailService.sendMerchantRegistrationAcceptance(merchant.getEmail(), merchant.getName());
        } catch (Exception e) {
            System.out.println("Error sending email: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public Merchant createMerchant(Merchant merchant) {
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
        merchant.setStatus(false);
        merchant = merchantRepository.save(merchant);
        MerchantForm merchantForm = new MerchantForm();
        merchantForm.setMerchant(merchant);
        merchantForm.setFormType(FormType.REGISTRATION);
        merchantForm.setCreateddDate(new Date());
        merchantFormRepository.save(merchantForm);
        return merchant;
    }

    @Override
    public Merchant getMerchantById(Long userId) {
        Merchant merchant = merchantRepository.findByUserId(userId);
        if(!CommonUtils.isEmpty(merchant)){
            merchant.setUser(null);
        }
        return merchant;
    }

    @Override
    public void upgradeToRoyalMerchant(MerchantForm merchantForm) {
        Merchant merchant = merchantForm.getMerchant();
        merchant.setIsRoyal(true);
        merchantRepository.save(merchant);
        try {
            emailService.sendRoyalMerchantRegistrationAcceptance(merchant.getEmail(), merchant.getName());
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public MerchantDTO getMerchantByMerchantID(Long id) {
        // tim nguoi ban tu id
        Optional<Merchant> merchantOptional = merchantRepository.findById(id);
        if(merchantOptional.isPresent()) {
            Merchant merchant = merchantOptional.get();
            // tim cac san pham cua nguoi ban do bang id
            List<Product> products = productService.getAllProductByShopId(merchant.getId());
            // tim cac binh luan cua nguoi ban do
            Set<Review> reviews = new HashSet<>();
            products.stream().forEach(product ->{
                reviews.addAll(reviewService.findByProductId(product.getId()));
            });

            // mapping sang DTO va tra ve
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
