package com.blps_lab1.demo.services.api;

import com.blps_lab1.demo.data.tables.ServiceOption;
import com.blps_lab1.demo.dto.CreateServiceOptionRequest;
import com.blps_lab1.demo.dto.ServiceOptionDto;

import java.util.List;

public interface IServiceOptionService {
    ServiceOptionDto create(CreateServiceOptionRequest request);
    List<ServiceOptionDto> findAll();
    List<ServiceOption> findEntitiesByIds(List<Long> ids);
}
