package com.blps_lab1.demo.controller;

import com.blps_lab1.demo.dto.CreatePlaceRequest;
import com.blps_lab1.demo.dto.PlaceDto;
import com.blps_lab1.demo.services.api.IPlaceService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/places")
public class PlaceController {
    private final IPlaceService placeService;

    public PlaceController(IPlaceService placeService) {
        this.placeService = placeService;
    }

    @GetMapping("/town/{town}")
    public List<PlaceDto> getPlacesByTown(@PathVariable("town") String town) {
        if (town == null || town.trim().isBlank()) {
            throw new IllegalArgumentException("Town parameter cannot be empty");
        }
        return placeService.findByTown(town);
    }

    @GetMapping("/rating")
    public List<PlaceDto> getPlacesByRating() {
        return placeService.findAllSortedByRating();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlaceDto> getById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(placeService.findPlace(id));
    }

    @PostMapping
    public ResponseEntity<PlaceDto> create(@Valid @RequestBody CreatePlaceRequest request) {
        return ResponseEntity.ok(placeService.create(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        placeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}