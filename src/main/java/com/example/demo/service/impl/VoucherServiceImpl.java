package com.example.demo.service.impl;

import com.example.demo.model.Product;
import com.example.demo.model.Variant;
import com.example.demo.model.Voucher;
import com.example.demo.model.VoucherCondition;
import com.example.demo.repository.VoucherRepository;
import com.example.demo.service.VoucherService;
import com.example.demo.service.dto.OrderDTO;
import com.example.demo.utils.CodeUtils;
import com.example.demo.utils.enumeration.ConditionType;
import com.example.demo.utils.enumeration.VoucherType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class VoucherServiceImpl implements VoucherService {

    @Autowired
    private VoucherRepository voucherRepository;
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public List<Voucher> findAll() {
        return voucherRepository.findAll();
    }

    @Override
    public Voucher findById(Long id) {
        return voucherRepository.findById(id).orElse(null);
    }

    @Override
    public void deleteById(Long id) {
        voucherRepository.deleteById(id);
    }

    @Override
    public Voucher save(Voucher entity) {
        return voucherRepository.save(entity);
    }

    @Override
    public Voucher createVoucher(Voucher voucher) {
        voucher.setCode(CodeUtils.generateVoucherCode(namedParameterJdbcTemplate));
        return save(voucher);
    }

    @Override
    public Voucher updateVoucher(Long id, Voucher voucher) {
        Voucher existingVoucher = findById(id);
        if (existingVoucher != null) {
            existingVoucher.setName(voucher.getName());
            existingVoucher.setCode(voucher.getCode());
            existingVoucher.setVoucherType(voucher.getVoucherType());
            existingVoucher.setVoucherCondition(voucher.getVoucherCondition());
            existingVoucher.setValueCondition(voucher.getValueCondition());
            existingVoucher.setDiscount(voucher.getDiscount());
            return save(existingVoucher);
        }
        return null;
    }

    @Override
    public List<Voucher> findActiveVouchers() {
        return findAll().stream()
                .filter(Voucher::getActive)
                .filter(voucher -> voucher.getMerchant() == null)
                .collect(Collectors.toList());
    }

    @Override
    public List<Voucher> findExpiredVouchers() {
        // Assuming there's an 'expirationDate' field in the Voucher entity
        LocalDate now = LocalDate.now();
        return findAll().stream()
                .filter(v -> v.getExpirationDate().isBefore(Instant.now()))
                .collect(Collectors.toList());
    }

    @Override
    public Voucher findByCode(String code) {
        return findAll().stream()
                .filter(v -> v.getCode().equals(code))
                .findFirst()
                .orElse(null);
    }

    @Override
    public boolean isVoucherValid(String code) {
        Voucher voucher = findByCode(code);
        if (voucher == null) return false;
        return voucher.getActive() && !voucher.getExpirationDate().isBefore(Instant.now());
    }

    @Override
    public void deactivateVoucher(Long id) {
        Voucher voucher = findById(id);
        if (voucher != null) {
            voucher.setActive(false);
            save(voucher);
        }
    }

    @Override
    public List<Voucher> findVouchersByType(VoucherType type) {
        return findAll().stream()
                .filter(v -> v.getVoucherType() == type)
                .collect(Collectors.toList());
    }

    @Override
    public List<Voucher> findVouchersByCondition(VoucherCondition condition) {
        return findAll().stream()
                .filter(v -> v.getVoucherCondition() == condition)
                .collect(Collectors.toList());
    }

    @Override
    public HashMap<String, List<Voucher>> getAbleVoucher(List<Variant> variants) {
        return null;
    }

    @Override
    public List<Voucher> getAbleVoucherByShop(List<Variant> variants) {
        if (variants.isEmpty()) {
            return new ArrayList<>();
        }
        
        Product firstProduct = variants.get(0).getProduct();
        if (firstProduct == null || firstProduct.getMerchant() == null) {
            return new ArrayList<>();
        }
        
        List<Voucher> shopVouchers = new ArrayList<>(firstProduct.getMerchant().getVouchers());
        List<Voucher> result = new ArrayList<>();
        
        List<OrderDTO> orderDTOS = createOrderDTOFromVariants(variants);
        for (OrderDTO orderDTO: orderDTOS){
            for (Voucher voucher : shopVouchers) {
                if (checkVoucherAvailable(voucher, orderDTO)) {
                    result.add(voucher);
                }
            }
        }
        return result;
    }
    
    @Override
    public List<Voucher> getAbleVoucherBySystem(List<Variant> variants) {
        List<Voucher> systemVouchers = findActiveVouchers();
        List<Voucher> result = new ArrayList<>();
        
        List<OrderDTO> orderDTO = createOrderDTOFromVariants(variants);
        
//        for (Voucher voucher : systemVouchers) {
//            if (checkVoucherAvailable(voucher)) {
//                result.add(voucher);
//            }
//        }
        return result;
    }

    private List<OrderDTO> createOrderDTOFromVariants(List<Variant> variants) {
        List<OrderDTO> orderDTOs = new ArrayList<>();

        if (!variants.isEmpty()) {
            Map<Long, List<Variant>> variantsByMerchant = variants.stream()
                    .filter(v -> v.getProduct() != null && v.getProduct().getMerchant() != null)
                    .collect(Collectors.groupingBy(v -> v.getProduct().getMerchant().getId()));

            for (Map.Entry<Long, List<Variant>> entry : variantsByMerchant.entrySet()) {
                Long merchantId = entry.getKey();
                List<Variant> merchantVariants = entry.getValue();

                OrderDTO orderDTO = new OrderDTO();
                orderDTO.setVariants(merchantVariants);
                orderDTO.setMerchantNumber(merchantId);

                double totalPrice = merchantVariants.stream()
                        .mapToDouble(v -> v.getPrice() * v.getQuantity())
                        .sum();
                orderDTO.setTotal(String.valueOf(totalPrice));

                // Set merchant name if available
                if (!merchantVariants.isEmpty() && merchantVariants.get(0).getProduct().getMerchant() != null) {
                    orderDTO.setMerchantName(merchantVariants.get(0).getProduct().getMerchant().getName());
                }

                orderDTOs.add(orderDTO);
            }
        }

        return orderDTOs;
    }
    
    private Boolean checkVoucherAvailable(Voucher voucher, OrderDTO orderDTO) {
        if (!voucher.getActive() || voucher.getExpirationDate().isBefore(Instant.now())) {
            return false;
        }
    
        if (voucher.getValueCondition() != null) {
            return false;
        }
    
        ConditionType voucherCondition = voucher.getVoucherCondition().getConditionType();
        switch (voucherCondition) {
            case ONLY_CATEGORY:
                return orderDTO.getVariants().stream().anyMatch(variant -> 
                    voucher.getVoucherCondition().getCategory().equals(variant.getProduct().getCategory()));
            case ONLY_PRODUCT:
                return orderDTO.getVariants().stream().anyMatch(variant -> 
                    variant.getProduct().equals(voucher.getVoucherCondition().getProduct()));
            case ONLY_SHOP:
                return orderDTO.getMerchantNumber().equals(voucher.getMerchant().getId());
            case ALL_AVAILABLE:
                return true;
            case MIN_COST:
                double orderTotal = orderDTO.getVariants().stream()
                        .mapToDouble(v -> v.getPrice() * v.getQuantity())
                        .sum();
                return voucher.getVoucherCondition().getMinPrice() <= orderTotal;
            default:
                return false;
        }
    }
    
    private static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }
}
