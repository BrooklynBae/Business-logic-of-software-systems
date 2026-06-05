package com.blps_lab1.demo.services;

import com.blps_lab1.demo.data.repository.OwnerRepository;
import com.blps_lab1.demo.data.tables.Owner;
import com.blps_lab1.demo.dto.CreateOwnerRequest;
import com.blps_lab1.demo.dto.OwnerDto;
import com.blps_lab1.demo.exception.NotFoundException;
import com.blps_lab1.demo.services.api.IOwnerService;
import com.blps_lab1.demo.security.XmlUserRegistry;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class OwnerService implements IOwnerService {
    private final OwnerRepository ownerRepository;
    private final XmlUserRegistry xmlUserRegistry;

    public OwnerService(OwnerRepository ownerRepository, XmlUserRegistry xmlUserRegistry) {
        this.ownerRepository = ownerRepository;
        this.xmlUserRegistry = xmlUserRegistry;
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
    @Transactional(rollbackFor = Exception.class)
    @PreAuthorize("permitAll()")
    public OwnerDto create(CreateOwnerRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request body cannot be null");
        }

        Owner owner = new Owner();
        owner.setName(request.getName());
        owner.setLogin(request.getLogin());
        owner.setRequirenmentsMessage(
                request.getRequirenmentsMessage() != null ? request.getRequirenmentsMessage() : false
        );
        owner.setRequirenmentsPhoto(
                request.getRequirenmentsPhoto() != null ? request.getRequirenmentsPhoto() : false
        );

        Owner saved = ownerRepository.save(owner);

        xmlUserRegistry.registerUserInXml(
                request.getLogin(),
                request.getPassword(),
                List.of("ROLE_OWNER"),
                List.of("PERM_MANAGE_OWN_PLACES")
        );

        return toDto(saved);
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public OwnerDto findById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }
        return toDto(findEntityById(id));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @PreAuthorize("hasAuthority('PERM_MANAGE_USERS') or @ownerRepository.findById(#id).orElse(null)?.getLogin() == authentication.name")
    public void delete(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }
        findEntityById(id);
        ownerRepository.deleteById(id);
    }

    @Override
    public Owner findEntityById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Id cannot be null");
        }
        return ownerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Owner not found with id = " + id));
    }
}