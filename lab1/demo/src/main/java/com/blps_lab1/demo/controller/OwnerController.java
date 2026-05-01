package com.blps_lab1.demo.controller;

import com.blps_lab1.demo.dto.CreateOwnerRequest;
import com.blps_lab1.demo.dto.OwnerDto;
import com.blps_lab1.demo.services.api.IOwnerService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/owners")
public class OwnerController {

    private final IOwnerService ownerService;

    public OwnerController(IOwnerService ownerService) {
        this.ownerService = ownerService;
    }

    @PostMapping
    public ResponseEntity<OwnerDto> create(@RequestBody CreateOwnerRequest request) {
        OwnerDto response = ownerService.create(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OwnerDto> getById(@PathVariable Long id) {
        OwnerDto response = ownerService.findById(id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        ownerService.delete(id);
        return ResponseEntity.noContent().build();
    }
}