package com.example.demo.service;

import com.example.demo.model.Address;
import java.util.List;

public interface AddressService extends GenerateService<Address> {
    List<Address> saveUserAddresses( Address address,Long userId);

}
