package com.blps_lab1.demo.controller;

import com.blps_lab1.demo.dto.CreatePlaceRequest;
import com.blps_lab1.demo.services.PlaceService;
import com.blps_lab1.demo.dto.PlaceDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/places")
public class MainController {
    private final PlaceService placeService;

    public MainController(PlaceService placeService) {
        this.placeService = placeService;
    }
    @GetMapping("/town/{town}")
    public List<PlaceDto> getPlacesByTown(@PathVariable String town) {
        return placeService.findByTown(town);
    }

    @GetMapping("/rating")
    public List<PlaceDto> getPlacesByRating() {
        return placeService.findAllSortedByRating();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlaceDto> getById(@PathVariable Long id) {
        PlaceDto response = placeService.findPlace(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<PlaceDto> create(@RequestBody CreatePlaceRequest request) {
        PlaceDto response = placeService.create(request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        placeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
