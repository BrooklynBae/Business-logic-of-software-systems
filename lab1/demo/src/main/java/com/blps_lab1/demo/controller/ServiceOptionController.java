package com.blps_lab1.demo.controller;

import com.blps_lab1.demo.dto.CreateServiceOptionRequest;
import com.blps_lab1.demo.dto.ServiceOptionDto;
import com.blps_lab1.demo.services.api.IServiceOptionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/service-options")
public class ServiceOptionController {
    private final IServiceOptionService serviceOptionService;

    public ServiceOptionController(IServiceOptionService serviceOptionService) {
        this.serviceOptionService = serviceOptionService;
    }

    @GetMapping
    public ResponseEntity<List<ServiceOptionDto>> findAll() {
        return ResponseEntity.ok(serviceOptionService.findAll());
    }
}
