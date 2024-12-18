package com.example.demo.service;

import com.example.demo.model.Variant;
import com.example.demo.model.Voucher;
import com.example.demo.model.VoucherCondition;
import com.example.demo.service.dto.OrderDTO;
import com.example.demo.service.dto.VoucherDTO;
import com.example.demo.utils.enumeration.VoucherType;

import java.util.HashMap;
import java.util.List;

public interface VoucherService extends GenerateService<Voucher> {

    Voucher createVoucher(VoucherDTO voucherDTO);

    Voucher updateVoucher(Long id, Voucher voucher);
    List<Voucher> findActiveVouchers();
    List<Voucher> findExpiredVouchers();
    Voucher findByCode(String code);
    boolean isVoucherValid(String code);
    void deactivateVoucher(Long id);
    List<Voucher> findVouchersByType(VoucherType type);
    List<Voucher> findVouchersByCondition(VoucherCondition condition);

    HashMap<String, List<Voucher>> getAbleVoucher(List<Variant> variants);

    List<Voucher> getAbleVoucherByShop(List<Variant> variants);

    List<Voucher> getAbleVoucherBySystem(List<Variant> variants);

    List<Voucher> getVouchersByMerchant(Long merchantId);

    List<Voucher> getVouchersSystem();
}
