package com.example.demo.repository;

import com.example.demo.entity.EmailConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailConfigurationRepository extends JpaRepository<EmailConfiguration, Long> {
    EmailConfiguration findTopByOrderByIdDesc();
}