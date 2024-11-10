package com.example.demo.repository;

import com.example.demo.model.VoucherCondition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface VoucherConditionRepository extends JpaRepository<VoucherCondition,Long> {
}
