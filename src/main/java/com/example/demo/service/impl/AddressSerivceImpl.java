package com.example.demo.service.impl;

import com.example.demo.model.Address;
import com.example.demo.model.Product;
import com.example.demo.model.User;
import com.example.demo.repository.AddressRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.AddressService;
import com.example.demo.utils.ObjectUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;


@Service
public class AddressSerivceImpl implements AddressService {

    @Autowired
    AddressRepository addressRepository;
    @Autowired
    private UserRepository userRepository;

    @Override
    public List<Address> findAll() {
        return addressRepository.findAll();
    }

    @Override
    public Address findById(Long id) {
        return addressRepository.findById(id).get();
    }

    @Override
    public void deleteById(Long id) {
         addressRepository.deleteById(id);
    }

    @Override
    public Address save(Address entity) {
         addressRepository.save(entity);
         return null;
    }

    @Override
public List<Address> saveUserAddresses(Address address, Long userId) {
    User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

    final Long addressId = address.getId();
    boolean addressExists = user.getAddresses().stream()
            .anyMatch(a -> a.getId() != null && a.getId().equals(addressId));

    if (!addressExists) {
        address = addressRepository.save(address);
        user.getAddresses().add(address);
        user = userRepository.save(user);
        Address finalAddress = address;
        user.getAddresses().forEach(a -> {
            if(!Objects.equals(a.getId(), finalAddress.getId())) {
                a.setIsDefault(false);
                addressRepository.save(a);
            }
        });
    } else {
        user.getAddresses().stream()
                .filter(a -> a.getId().equals(addressId))
                .findFirst()
                .ifPresent(existingAddress -> {

                });
        user = userRepository.save(user);
    }

    return user.getAddresses().stream().collect(Collectors.toList());
}
}
