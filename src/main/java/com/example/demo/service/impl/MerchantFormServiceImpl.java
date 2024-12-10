package com.example.demo.service.impl;

import com.example.demo.model.Merchant;
import com.example.demo.model.MerchantForm;
import com.example.demo.repository.MerchantFormRepository;
import com.example.demo.service.EmailService;
import com.example.demo.service.MerchantFormService;
import com.example.demo.service.MerchantService;
import com.example.demo.service.OrderService;
import com.example.demo.utils.enumeration.FormStatus;
import com.example.demo.utils.enumeration.FormType;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MerchantFormServiceImpl implements MerchantFormService {

    @Autowired
    private MerchantFormRepository merchantFormRepository;
    @Autowired
    private EmailService emailService;

    @Autowired
    private MerchantService merchantService;
    @Autowired
    private OrderService orderService;


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
        return merchantFormRepository.findAll();
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

    @Override
    public void approvalFormRegister(Long id) throws MessagingException {
        MerchantForm merchantForm = merchantFormRepository
                .findById(id).orElseThrow(() -> new RuntimeException("MerchantForm not found with id: " + id));
        merchantForm.getMerchant().setStatus(true);
        merchantForm.setStatus(FormStatus.APPROVED);
        merchantFormRepository.save(merchantForm);
        emailService.sendRoyalMerchantRegistrationAcceptance(merchantForm.getMerchant().getEmail(), merchantForm.getMerchant().getName());

    }

    @Override
    public void approvalFormUpgrade(Long id) throws MessagingException {
        MerchantForm merchantForm = merchantFormRepository
                .findById(id).orElseThrow(() -> new RuntimeException("MerchantForm not found with id: " + id));
        Merchant merchant = merchantService.findById(merchantForm.getMerchant().getId());
        double revenue = 0.0;
        Map<Integer,Double> revenueMap =
                orderService.getDailyRevenueForQuarter(merchant.getId(), LocalDate.now().getYear(), LocalDate.now().getMonthValue()/4+1);
        for(Map.Entry<Integer, Double> entry : revenueMap.entrySet()){
            revenue += entry.getValue();
        }
        if( revenue > 1000000000){
            merchant.setIsRoyal(true);
            merchantService.save(merchant);
            merchantForm.getMerchant().setIsRoyal(true);
            merchantForm.setStatus(FormStatus.APPROVED);
            merchantFormRepository.save(merchantForm);
            emailService.sendRoyalMerchantRegistrationAcceptance(merchantForm.getMerchant().getEmail(), merchantForm.getMerchant().getName());
        }else{
            throw new MessagingException("Not enough revenue to upgrade to Royal Merchant");
        }

    }
}