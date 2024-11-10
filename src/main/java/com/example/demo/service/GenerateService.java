package com.example.demo.service;

import com.example.demo.model.Product;

import java.util.List;

public interface GenerateService<E> {
    List<E> findAll();

    E findById(Long id);

    void deleteById(Long id);

    E save(E entity);
}
