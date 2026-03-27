package com.blps_lab1.demo.services;

import com.blps_lab1.demo.data.repository.OwnerRepository;
import com.blps_lab1.demo.data.tables.Owner;
import com.blps_lab1.demo.dto.CreateOwnerRequest;
import com.blps_lab1.demo.dto.OwnerDto;
import org.springframework.stereotype.Service;

@Service
public class OwnerService {
    private final OwnerRepository ownerRepository;

    public OwnerService(OwnerRepository ownerRepository) {
        this.ownerRepository = ownerRepository;
    }

    private OwnerDto toDto(Owner owner) {
        return new OwnerDto(
                owner.getId(),
                owner.getName(),
                owner.getRequirenmentsMessage(),
                owner.getRequirenmentsPhoto()
        );
    }

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

    public OwnerDto findById(Long id) {
        Owner owner = ownerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Owner not found with id = " + id));
        return toDto(owner);
    }

    public void delete(Long id) {
        if (!ownerRepository.existsById(id)) {
            throw new RuntimeException("Owner not found with id = " + id);
        }
        ownerRepository.deleteById(id);
    }
}