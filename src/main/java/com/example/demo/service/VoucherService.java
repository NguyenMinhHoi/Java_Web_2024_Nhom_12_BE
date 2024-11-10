package com.example.demo.service;

import com.example.demo.model.Voucher;
import com.example.demo.model.VoucherCondition;
import com.example.demo.utils.enumeration.VoucherType;

import java.util.List;

public interface VoucherService extends GenerateService<Voucher> {
    Voucher createVoucher(Voucher voucher);
    Voucher updateVoucher(Long id, Voucher voucher);
    List<Voucher> findActiveVouchers();
    List<Voucher> findExpiredVouchers();
    Voucher findByCode(String code);
    boolean isVoucherValid(String code);
    void deactivateVoucher(Long id);
    List<Voucher> findVouchersByType(VoucherType type);
    List<Voucher> findVouchersByCondition(VoucherCondition condition);
}
