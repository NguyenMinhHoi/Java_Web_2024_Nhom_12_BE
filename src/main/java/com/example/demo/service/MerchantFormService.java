package com.example.demo.service;

import com.example.demo.model.MerchantForm;
import com.example.demo.utils.enumeration.FormStatus;
import com.example.demo.utils.enumeration.FormType;

import java.util.List;
import java.util.Optional;

public interface MerchantFormService extends GenerateService<MerchantForm> {
    List<MerchantForm> findPendingMerchantForms();
    MerchantForm updateMerchantFormStatus(Long id, FormStatus status);
    List<MerchantForm> findByFormType(FormType formType);

}