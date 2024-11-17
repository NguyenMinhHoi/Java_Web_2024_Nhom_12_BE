package com.example.demo.service.impl;

import com.example.demo.model.Product;
import com.example.demo.model.Voucher;
import com.example.demo.model.VoucherCondition;
import com.example.demo.repository.VoucherRepository;
import com.example.demo.service.VoucherService;
import com.example.demo.utils.enumeration.VoucherType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VoucherServiceImpl implements VoucherService {

    @Autowired
    private VoucherRepository voucherRepository;

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
        // Assuming there's an 'active' field in the Voucher entity
        return findAll().stream()
                .filter(Voucher::getActive)
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
}
