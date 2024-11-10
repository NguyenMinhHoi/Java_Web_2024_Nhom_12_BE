package com.example.demo.repository;

import com.example.demo.model.MerchantForm;
import com.example.demo.utils.enumeration.FormStatus;
import com.example.demo.utils.enumeration.FormType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MerchantFormRepository extends JpaRepository<MerchantForm, Long> {
    List<MerchantForm> findByStatus(FormStatus status);
    List<MerchantForm> findByFormType(FormType formType);

}