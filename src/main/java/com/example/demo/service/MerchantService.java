package com.example.demo.service;

import com.example.demo.model.*;

import com.example.demo.model.Merchant;
import com.example.demo.service.dto.MerchantDTO;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface MerchantService extends GenerateService<Merchant> {
    void approveMerchantRegistration(MerchantForm merchantForm);

    void approveMerchantRegistration(Merchant merchant);
    Merchant createMerchant(Merchant merchant);
    Merchant getMerchantById(Long id);


    void upgradeToRoyalMerchant(MerchantForm merchantForm);

    MerchantDTO getMerchantByMerchantID(Long id);

}
