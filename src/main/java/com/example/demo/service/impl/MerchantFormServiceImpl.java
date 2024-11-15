package com.example.demo.service.impl;

import com.example.demo.model.MerchantForm;
import com.example.demo.repository.MerchantFormRepository;
import com.example.demo.service.MerchantFormService;
import com.example.demo.utils.enumeration.FormStatus;
import com.example.demo.utils.enumeration.FormType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MerchantFormServiceImpl implements MerchantFormService {

    @Autowired
    private MerchantFormRepository merchantFormRepository;

    // Implement other methods from GenerateService...

    @Override
    public List<MerchantForm> findPendingMerchantForms() {
        return merchantFormRepository.findByStatus(FormStatus.PENDING);
    }

    @Override
    public MerchantForm updateMerchantFormStatus(Long id, FormStatus newStatus) {
        MerchantForm merchantForm = findById(id);
        if (merchantForm != null) {
            merchantForm.setStatus(newStatus);
            return save(merchantForm);
        }
        throw new RuntimeException("MerchantForm not found with id: " + id);
    }

    @Override
    public List<MerchantForm> findByFormType(FormType formType) {
        return merchantFormRepository.findByFormType(formType);
    }

    @Override
    public List<MerchantForm> findAll() {
        return List.of();
    }

    @Override
    public MerchantForm findById(Long id) {
        return null;
    }

    @Override
    public void deleteById(Long id) {

    }

    @Override
    public MerchantForm save(MerchantForm entity) {
        return null;
    }
}