package com.example.demo.repository;

import com.example.demo.model.OptionProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OptionRepository extends JpaRepository<OptionProduct,Long> {
}
