package com.blps_lab1.demo.services;

import com.blps_lab1.demo.data.repository.ServiceOptionRepository;
import com.blps_lab1.demo.data.tables.ServiceOption;
import com.blps_lab1.demo.dto.CreateServiceOptionRequest;
import com.blps_lab1.demo.dto.ServiceOptionDto;
import com.blps_lab1.demo.exception.BadRequestException;
import com.blps_lab1.demo.services.api.IServiceOptionService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class ServiceOptionService implements IServiceOptionService {
    private final ServiceOptionRepository serviceOptionRepository;

    public ServiceOptionService(ServiceOptionRepository serviceOptionRepository) {
        this.serviceOptionRepository = serviceOptionRepository;
    }

    @Override
    @PreAuthorize("hasAuthority('PERM_PROCESS_PAYMENT')")
    public List<ServiceOptionDto> findAll() {
        return serviceOptionRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public List<ServiceOption> findEntitiesByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }

        List<ServiceOption> options = serviceOptionRepository.findAllById(ids);
        if (options.size() != ids.size()) {
            throw new BadRequestException("Some service options were not found");
        }
        return options;
    }

    private ServiceOptionDto toDto(ServiceOption serviceOption) {
        return ServiceOptionDto.builder()
                .id(serviceOption.getId())
                .name(serviceOption.getName())
                .description(serviceOption.getDescription())
                .pricePerDay(serviceOption.getPricePerDay())
                .build();
    }
}
