package com.blps_lab1.demo.services.api;

import com.blps_lab1.demo.data.tables.Owner;
import com.blps_lab1.demo.dto.CreateOwnerRequest;
import com.blps_lab1.demo.dto.OwnerDto;

public interface IOwnerService {
    OwnerDto create(CreateOwnerRequest request);
    OwnerDto findById(Long id);
    void delete(Long id);
    Owner findEntityById(Long id);
}
