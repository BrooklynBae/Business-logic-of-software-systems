package com.blps_lab1.demo.services;

import com.blps_lab1.demo.data.repository.OwnerRepository;
import com.blps_lab1.demo.data.tables.Owner;
import com.blps_lab1.demo.dto.CreateOwnerRequest;
import com.blps_lab1.demo.dto.OwnerDto;
import com.blps_lab1.demo.exception.NotFoundException;
import com.blps_lab1.demo.services.api.IOwnerService;
import org.springframework.stereotype.Service;

@Service
public class OwnerService implements IOwnerService {
    private final OwnerRepository ownerRepository;

    public OwnerService(OwnerRepository ownerRepository) {
        this.ownerRepository = ownerRepository;
    }

    private OwnerDto toDto(Owner owner) {
        return OwnerDto.builder()
                .id(owner.getId())
                .name(owner.getName())
                .requirenmentsMessage(owner.getRequirenmentsMessage())
                .requirenmentsPhoto(owner.getRequirenmentsPhoto())
                .build();
    }

    @Override
    public OwnerDto create(CreateOwnerRequest request) {
        Owner owner = new Owner();
        owner.setName(request.getName());
        owner.setRequirenmentsMessage(
                request.getRequirenmentsMessage() != null ? request.getRequirenmentsMessage() : false
        );
        owner.setRequirenmentsPhoto(
                request.getRequirenmentsPhoto() != null ? request.getRequirenmentsPhoto() : false
        );

        Owner saved = ownerRepository.save(owner);
        return toDto(saved);
    }

    @Override
    public OwnerDto findById(Long id) {
        return toDto(findEntityById(id));
    }

    @Override
    public void delete(Long id) {
        findEntityById(id);
        ownerRepository.deleteById(id);
    }

    @Override
    public Owner findEntityById(Long id) {
        return ownerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Owner not found with id = " + id));
    }
}