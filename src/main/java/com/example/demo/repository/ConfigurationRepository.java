package com.example.demo.repository;

import com.example.demo.entity.ConfigurationParameter;
import com.example.demo.utils.Const;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface ConfigurationRepository extends JpaRepository<ConfigurationParameter, Long> {
}